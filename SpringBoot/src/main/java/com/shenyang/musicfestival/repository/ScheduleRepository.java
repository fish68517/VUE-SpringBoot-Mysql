package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Schedule entity
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByFestivalIdOrderByStartTimeAsc(Long festivalId);

    List<Schedule> findByFestivalIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualOrderByStartTimeAsc(
        Long festivalId, LocalDateTime startTime, LocalDateTime endTime);

}
