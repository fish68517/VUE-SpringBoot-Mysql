package com.medical.internship.repository;

import com.medical.internship.entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 周记数据访问接口
 */
@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
    
    /**
     * 根据实习记录ID查询周记列表
     */
    List<WeeklyReport> findByInternshipId(Long internshipId);
    
    /**
     * 根据实习记录ID和周次查询周记
     */
    java.util.Optional<WeeklyReport> findByInternshipIdAndWeekNumber(Long internshipId, Integer weekNumber);
    
    /**
     * 根据状态查询周记列表
     */
    List<WeeklyReport> findByStatus(String status);
}
