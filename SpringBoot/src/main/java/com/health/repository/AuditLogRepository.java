package com.health.repository;

import com.health.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志数据访问层接口
 * 提供审计日志相关的数据库操作方法
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * 按用户ID查询所有审计日志
     * @param userId 用户ID
     * @return 审计日志列表
     */
    List<AuditLog> findByUserId(Long userId);

    /**
     * 按操作类型查询审计日志
     * @param action 操作类型
     * @return 审计日志列表
     */
    List<AuditLog> findByAction(String action);

    /**
     * 按资源查询审计日志
     * @param resource 资源
     * @return 审计日志列表
     */
    List<AuditLog> findByResource(String resource);

    /**
     * 按时间范围查询审计日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 按用户ID和时间范围查询审计日志
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> findByUserIdAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
