package com.naxi.repository;

import com.naxi.entity.AdminAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    /**
     * 按管理员ID查询操作日志
     */
    Page<AdminAuditLog> findByAdminId(Long adminId, Pageable pageable);

    /**
     * 按操作类型查询操作日志
     */
    Page<AdminAuditLog> findByOperationType(String operationType, Pageable pageable);

    /**
     * 按管理员ID和操作类型查询操作日志
     */
    Page<AdminAuditLog> findByAdminIdAndOperationType(Long adminId, String operationType, Pageable pageable);
}
