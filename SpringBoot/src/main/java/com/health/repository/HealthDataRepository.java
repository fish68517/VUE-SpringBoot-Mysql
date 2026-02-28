package com.health.repository;

import com.health.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康数据访问层接口
 * 提供健康数据相关的数据库操作方法
 */
@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {

    /**
     * 按用户ID查询所有健康数据
     * @param userId 用户ID
     * @return 健康数据列表
     */
    List<HealthData> findByUserId(Long userId);

    /**
     * 按用户ID和数据类型查询健康数据
     * @param userId 用户ID
     * @param dataType 数据类型
     * @return 健康数据列表
     */
    List<HealthData> findByUserIdAndDataType(Long userId, String dataType);

    /**
     * 按时间范围查询用户的健康数据
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 健康数据列表
     */
    @Query("SELECT h FROM HealthData h WHERE h.userId = :userId AND h.recordedAt BETWEEN :startTime AND :endTime ORDER BY h.recordedAt DESC")
    List<HealthData> findByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按用户ID查询最新的健康数据
     * @param userId 用户ID
     * @return 最新的健康数据列表（按记录时间倒序）
     */
    @Query(value = "SELECT * FROM health_data WHERE user_id = :userId ORDER BY recorded_at DESC LIMIT :limit", nativeQuery = true)
    List<HealthData> findLatestByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 按数据类型查询所有健康数据
     * @param dataType 数据类型
     * @return 健康数据列表
     */
    List<HealthData> findByDataType(String dataType);
}
