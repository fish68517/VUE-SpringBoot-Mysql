package com.naxi.repository;

import com.naxi.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    /**
     * 按日志级别查询系统日志
     */
    Page<SystemLog> findByLogLevel(SystemLog.LogLevel logLevel, Pageable pageable);
}
