package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.ReservationDTO;
import com.studyroom.dto.Result;
import com.studyroom.entity.Reservation;
import com.studyroom.entity.Seat;
import com.studyroom.mapper.ReservationMapper;
import com.studyroom.mapper.SeatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final SeatMapper seatMapper;
    private final UserService userService;

    public ReservationService(ReservationMapper reservationMapper, SeatMapper seatMapper, UserService userService) {
        this.reservationMapper = reservationMapper;
        this.seatMapper = seatMapper;
        this.userService = userService;
    }

    @Transactional
    public Result<String> createReservation(Long userId, ReservationDTO dto) {
        // 验证座位是否存在且可用
        Seat seat = seatMapper.selectById(dto.getSeatId());
        if (seat == null || seat.getStatus() != 1) {
            return Result.error("座位不可用");
        }

        // 验证时间
        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            return Result.error("结束时间必须大于开始时间");
        }

        if (dto.getDate().isBefore(LocalDate.now())) {
            return Result.error("不能预约过去的日期");
        }

        // 检查时间段是否已被预约
        List<Reservation> conflicts = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getSeatId, dto.getSeatId())
                        .eq(Reservation::getDate, dto.getDate())
                        .in(Reservation::getStatus, Arrays.asList(1, 2))
                        .and(wrapper -> wrapper
                                .apply("(start_time < {0} AND end_time > {1})", dto.getEndTime(), dto.getStartTime())
                        )
        );

        if (!conflicts.isEmpty()) {
            return Result.error("该时间段已被预约");
        }

        // 检查用户当天是否已有预约（限制每天最多预约3次）
        Long userReservationCount = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getUserId, userId)
                        .eq(Reservation::getDate, dto.getDate())
                        .ne(Reservation::getStatus, 0) // 不算已取消的
        );

        if (userReservationCount >= 3) {
            return Result.error("每天最多预约3次");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeatId(dto.getSeatId());
        reservation.setDate(dto.getDate());
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setStatus(1);

        reservationMapper.insert(reservation);
        return Result.success("预约成功");
    }

    public Result<String> cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return Result.error("预约不存在");
        }

        if (!reservation.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }

        if (reservation.getStatus() != 1) {
            return Result.error("该预约无法取消");
        }

        reservation.setStatus(0);
        reservationMapper.updateById(reservation);
        return Result.success("取消成功");
    }

    @Transactional
    public Result<String> checkIn(Long userId, Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return Result.error("预约不存在");
        }

        if (!reservation.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }

        if (reservation.getStatus() != 1) {
            return Result.error("该预约状态不允许签到");
        }

        // 验证是否在预约时间范围内（允许提前15分钟签到）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationStart = LocalDateTime.of(reservation.getDate(), reservation.getStartTime());
        LocalDateTime reservationEnd = LocalDateTime.of(reservation.getDate(), reservation.getEndTime());

        if (now.isBefore(reservationStart.minusMinutes(15))) {
            return Result.error("还未到签到时间，最早可提前15分钟签到");
        }

        if (now.isAfter(reservationEnd)) {
            return Result.error("预约已过期");
        }

        reservation.setStatus(2);
        reservation.setCheckInTime(now);
        reservationMapper.updateById(reservation);
        return Result.success("签到成功");
    }

    @Transactional
    public Result<String> checkOut(Long userId, Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return Result.error("预约不存在");
        }

        if (!reservation.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }

        if (reservation.getStatus() != 2) {
            return Result.error("该预约状态不允许签退");
        }

        LocalDateTime now = LocalDateTime.now();
        reservation.setStatus(3);
        reservation.setCheckOutTime(now);
        reservationMapper.updateById(reservation);

        // 计算学习时长并更新用户
        long minutes = Duration.between(reservation.getCheckInTime(), now).toMinutes();
        if (minutes > 0) {
            userService.addStudyTime(userId, (int) minutes);
        }

        return Result.success("签退成功，本次学习" + minutes + "分钟");
    }

    public Result<List<Map<String, Object>>> getUserReservations(Long userId) {
        List<Reservation> reservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getUserId, userId)
                        .orderByDesc(Reservation::getDate, Reservation::getStartTime)
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (Reservation r : reservations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("date", r.getDate());
            item.put("startTime", r.getStartTime());
            item.put("endTime", r.getEndTime());
            item.put("status", r.getStatus());
            item.put("checkInTime", r.getCheckInTime());
            item.put("checkOutTime", r.getCheckOutTime());
            item.put("createTime", r.getCreateTime());

            Seat seat = seatMapper.selectById(r.getSeatId());
            if (seat != null) {
                item.put("seatNo", seat.getSeatNo());
                item.put("area", seat.getArea());
            }
            result.add(item);
        }
        return Result.success(result);
    }

    public Result<List<Map<String, Object>>> getAllReservations(LocalDate date) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<Reservation>()
                .orderByDesc(Reservation::getDate, Reservation::getStartTime);

        if (date != null) {
            wrapper.eq(Reservation::getDate, date);
        }

        List<Reservation> reservations = reservationMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Reservation r : reservations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("userId", r.getUserId());
            item.put("date", r.getDate());
            item.put("startTime", r.getStartTime());
            item.put("endTime", r.getEndTime());
            item.put("status", r.getStatus());
            item.put("checkInTime", r.getCheckInTime());
            item.put("checkOutTime", r.getCheckOutTime());

            Seat seat = seatMapper.selectById(r.getSeatId());
            if (seat != null) {
                item.put("seatNo", seat.getSeatNo());
                item.put("area", seat.getArea());
            }
            result.add(item);
        }
        return Result.success(result);
    }

    @Transactional
    public Result<String> changeReservation(Long userId, Long reservationId, ReservationDTO dto) {
        Reservation oldReservation = reservationMapper.selectById(reservationId);
        if (oldReservation == null) {
            return Result.error("预约不存在");
        }

        if (!oldReservation.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }

        if (oldReservation.getStatus() != 1) {
            return Result.error("该预约状态不允许改签");
        }

        // 先取消原预约
        oldReservation.setStatus(0);
        reservationMapper.updateById(oldReservation);

        // 创建新预约
        return createReservation(userId, dto);
    }

    public Result<Map<String, Object>> getStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        // 今日预约数
        Long todayCount = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getDate, today)
                        .ne(Reservation::getStatus, 0)
        );

        // 本月预约数
        Long monthCount = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .ge(Reservation::getDate, monthStart)
                        .le(Reservation::getDate, today)
                        .ne(Reservation::getStatus, 0)
        );

        // 今日签到数
        Long todayCheckIn = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getDate, today)
                        .in(Reservation::getStatus, Arrays.asList(2, 3))
        );

        Map<String, Object> stats = new HashMap<>();
        stats.put("todayReservations", todayCount);
        stats.put("monthReservations", monthCount);
        stats.put("todayCheckIns", todayCheckIn);
        stats.put("date", today);

        return Result.success(stats);
    }
}
