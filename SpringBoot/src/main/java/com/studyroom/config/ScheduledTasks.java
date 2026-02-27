package com.studyroom.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.entity.Reservation;
import com.studyroom.mapper.ReservationMapper;
import com.studyroom.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final UserService userService;
    private final ReservationMapper reservationMapper;

    public ScheduledTasks(UserService userService, ReservationMapper reservationMapper) {
        this.userService = userService;
        this.reservationMapper = reservationMapper;
    }

    /**
     * 每月1日0点重置月度学习时长
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyStudyTime() {
        logger.info("开始重置月度学习时长...");
        userService.resetMonthlyStudyTime();
        logger.info("月度学习时长重置完成");
    }

    /**
     * 每天凌晨1点处理过期预约
     * 将未签到的已预约状态改为已取消
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void handleExpiredReservations() {
        logger.info("开始处理过期预约...");
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 获取昨天未签到的预约
        List<Reservation> expiredReservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getDate, yesterday)
                        .eq(Reservation::getStatus, 1) // 已预约但未签到
        );

        for (Reservation r : expiredReservations) {
            r.setStatus(0); // 标记为已取消
            reservationMapper.updateById(r);
        }

        logger.info("处理了{}条过期预约", expiredReservations.size());
    }

    /**
     * 每天凌晨2点自动签退未签退的预约
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoCheckOut() {
        logger.info("开始自动签退...");
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 获取昨天使用中但未签退的预约
        List<Reservation> inUseReservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getDate, yesterday)
                        .eq(Reservation::getStatus, 2) // 使用中
        );

        for (Reservation r : inUseReservations) {
            // 以预约结束时间作为签退时间
            LocalDateTime checkOutTime = LocalDateTime.of(r.getDate(), r.getEndTime());
            r.setStatus(3);
            r.setCheckOutTime(checkOutTime);
            reservationMapper.updateById(r);

            // 计算并更新学习时长
            long minutes = Duration.between(r.getCheckInTime(), checkOutTime).toMinutes();
            if (minutes > 0) {
                userService.addStudyTime(r.getUserId(), (int) minutes);
            }
        }

        logger.info("自动签退了{}条预约", inUseReservations.size());
    }
}
