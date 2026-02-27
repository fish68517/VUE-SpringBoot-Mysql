package com.agricultural.repository;

import com.agricultural.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 气象数据访问层接口
 */
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    
    /**
     * 根据地区查询气象数据
     */
    List<WeatherData> findByRegion(String region);
    
    /**
     * 根据地区和时间范围查询气象数据
     */
    @Query("SELECT w FROM WeatherData w WHERE w.region = :region AND w.recordedAt BETWEEN :startTime AND :endTime ORDER BY w.recordedAt DESC")
    List<WeatherData> findByRegionAndTimeRange(@Param("region") String region, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定地区最新的气象数据
     */
    @Query(value = "SELECT * FROM weather_data WHERE region = :region ORDER BY recorded_at DESC LIMIT 1", nativeQuery = true)
    WeatherData findLatestByRegion(@Param("region") String region);

    // 根据地区和记录时间精准查找数据
    WeatherData findByRegionAndRecordedAt(String region, LocalDateTime recordedAt);
}
