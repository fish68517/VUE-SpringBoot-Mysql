package com.campus.dto;

import com.campus.entity.AuditLog;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audit Log Data Transfer Object
 * Used for transferring audit log data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogDTO {

    /**
     * Audit log ID
     */
    private Long id;

    /**
     * Auditor ID (admin user)
     */
    private Long auditorId;

    /**
     * Auditor information (nested)
     */
    private UserDTO auditor;

    /**
     * Target ID (activity or fund proof)
     */
    private Long targetId;

    /**
     * Audit type
     */
    private AuditLog.AuditType auditType;

    /**
     * Audit status
     */
    private AuditLog.AuditStatus auditStatus;

    /**
     * Audit reason/comment
     */
    private String reason;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Convert entity to DTO
     */
    public static AuditLogDTO fromEntity(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .auditorId(auditLog.getAuditorId())
                .targetId(auditLog.getTargetId())
                .auditType(auditLog.getAuditType())
                .auditStatus(auditLog.getAuditStatus())
                .reason(auditLog.getReason())
                .createdAt(auditLog.getCreatedAt())
                .updatedAt(auditLog.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public AuditLog toEntity() {
        AuditLog auditLog = AuditLog.builder()
                .auditorId(this.auditorId)
                .targetId(this.targetId)
                .auditType(this.auditType)
                .auditStatus(this.auditStatus)
                .reason(this.reason)
                .build();
        auditLog.setId(this.id);
        return auditLog;
    }

}
