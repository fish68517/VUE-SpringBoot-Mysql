package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.Result;
import com.studyroom.entity.Reservation;
import com.studyroom.entity.Seat;
import com.studyroom.mapper.ReservationMapper;
import com.studyroom.mapper.SeatMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatMapper seatMapper;
    private final ReservationMapper reservationMapper;

    public SeatService(SeatMapper seatMapper, ReservationMapper reservationMapper) {
        this.seatMapper = seatMapper;
        this.reservationMapper = reservationMapper;
    }

    public Result<List<Seat>> listSeats() {
        List<Seat> seats = seatMapper.selectList(
                new LambdaQueryWrapper<Seat>().orderByAsc(Seat::getArea, Seat::getSeatNo)
        );
        return Result.success(seats);
    }

    public Result<List<Seat>> listSeatsByArea(String area) {
        List<Seat> seats = seatMapper.selectList(
                new LambdaQueryWrapper<Seat>()
                        .eq(Seat::getArea, area)
                        .eq(Seat::getStatus, 1)
                        .orderByAsc(Seat::getSeatNo)
        );
        return Result.success(seats);
    }

    public Result<Map<String, Object>> getAvailableSeats(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 获取所有可用座位
        List<Seat> allSeats = seatMapper.selectList(
                new LambdaQueryWrapper<Seat>().eq(Seat::getStatus, 1)
        );

        // 获取该时段已被预约的座位
        List<Reservation> reservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getDate, date)
                        .in(Reservation::getStatus, Arrays.asList(1, 2)) // 已预约或使用中
                        .and(wrapper -> wrapper
                                .apply("(start_time < {0} AND end_time > {1})", endTime, startTime)
                        )
        );

        Set<Long> reservedSeatIds = reservations.stream()
                .map(Reservation::getSeatId)
                .collect(Collectors.toSet());

        // 按区域分组
        Map<String, List<Map<String, Object>>> seatsByArea = new LinkedHashMap<>();
        for (String area : Arrays.asList("A", "B", "C", "D")) {
            List<Map<String, Object>> areaSeats = allSeats.stream()
                    .filter(seat -> seat.getArea().equals(area))
                    .map(seat -> {
                        Map<String, Object> seatInfo = new HashMap<>();
                        seatInfo.put("id", seat.getId());
                        seatInfo.put("seatNo", seat.getSeatNo());
                        seatInfo.put("area", seat.getArea());
                        seatInfo.put("available", !reservedSeatIds.contains(seat.getId()));
                        return seatInfo;
                    })
                    .collect(Collectors.toList());
            seatsByArea.put(area, areaSeats);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("seats", seatsByArea);
        result.put("date", date);
        result.put("startTime", startTime);
        result.put("endTime", endTime);

        return Result.success(result);
    }

    public Result<String> updateSeatStatus(Long seatId, Integer status) {
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null) {
            return Result.error("座位不存在");
        }
        seat.setStatus(status);
        seatMapper.updateById(seat);
        return Result.success(status == 1 ? "已启用" : "已禁用");
    }

    public Result<String> addSeat(Seat seat) {
        Seat existSeat = seatMapper.selectOne(
                new LambdaQueryWrapper<Seat>().eq(Seat::getSeatNo, seat.getSeatNo())
        );
        if (existSeat != null) {
            return Result.error("座位编号已存在");
        }
        seat.setStatus(1);
        seatMapper.insert(seat);
        return Result.success("添加成功");
    }

    public Result<String> deleteSeat(Long seatId) {
        // 检查是否有未完成的预约
        Long count = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getSeatId, seatId)
                        .in(Reservation::getStatus, Arrays.asList(1, 2))
        );
        if (count > 0) {
            return Result.error("该座位有未完成的预约，无法删除");
        }
        seatMapper.deleteById(seatId);
        return Result.success("删除成功");
    }
}
