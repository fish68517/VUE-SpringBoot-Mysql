package com.agricultural.repository;

import com.agricultural.entity.Warning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警数据访问层接口
 */
@Repository
public interface WarningRepository extends JpaRepository<Warning, Long> {
    
    /**
     * 根据地区查询预警列表
     */
    List<Warning> findByRegion(String region);
    
    /**
     * 根据预警类型查询预警列表
     */
    List<Warning> findByWarningType(String warningType);
    
    /**
     * 根据预警状态查询预警列表
     */
    List<Warning> findByStatus(Warning.WarningStatus status);
    
    /**
     * 根据地区和预警类型查询预警列表
     */
    List<Warning> findByRegionAndWarningType(String region, String warningType);
    
    /**
     * 根据地区和预警状态查询预警列表
     */
    List<Warning> findByRegionAndStatus(String region, Warning.WarningStatus status);
    
    /**
     * 根据地区、预警类型和状态查询预警列表
     */
    @Query("SELECT w FROM Warning w WHERE w.region = :region AND w.warningType = :warningType AND w.status = :status")
    List<Warning> findByRegionAndWarningTypeAndStatus(@Param("region") String region, 
                                                      @Param("warningType") String warningType, 
                                                      @Param("status") Warning.WarningStatus status);
    
    /**
     * 查询活跃的预警列表
     */
    @Query("SELECT w FROM Warning w WHERE w.status = 'ACTIVE' AND w.endTime > :currentTime ORDER BY w.severity DESC")
    List<Warning> findActiveWarnings(@Param("currentTime") LocalDateTime currentTime);
}
