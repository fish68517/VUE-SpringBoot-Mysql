package com.health.repository;

import com.health.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {

    List<HealthData> findByUserId(Long userId);

    List<HealthData> findByUserIdAndDataType(Long userId, String dataType);

    @Query("SELECT h FROM HealthData h WHERE h.userId = :userId AND h.recordedAt BETWEEN :startTime AND :endTime ORDER BY h.recordedAt DESC")
    List<HealthData> findByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query(value = "SELECT * FROM health_data WHERE user_id = :userId ORDER BY recorded_at DESC LIMIT :limit", nativeQuery = true)
    List<HealthData> findLatestByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    List<HealthData> findByDataType(String dataType);

    // 新增：根据审核状态查询数据（供医师端使用）
    List<HealthData> findByReviewStatusOrderByRecordedAtDesc(String reviewStatus);
}