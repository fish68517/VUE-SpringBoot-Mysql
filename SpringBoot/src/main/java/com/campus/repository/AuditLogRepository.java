package com.campus.repository;

import com.campus.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Audit Log Repository
 * Provides data access methods for AuditLog entity
 */
@Repository
public interface AuditLogRepository extends BaseRepository<AuditLog> {

    /**
     * Find audit logs by auditor ID
     */
    Page<AuditLog> findByAuditorId(Long auditorId, Pageable pageable);

    /**
     * Find audit logs by target ID
     */
    Page<AuditLog> findByTargetId(Long targetId, Pageable pageable);

    /**
     * Find audit logs by audit type
     */
    Page<AuditLog> findByAuditType(AuditLog.AuditType auditType, Pageable pageable);

}
