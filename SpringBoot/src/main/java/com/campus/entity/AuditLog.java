package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Audit Log Entity
 * Records audit operations on activities and fund proofs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_auditor_id", columnList = "auditor_id"),
        @Index(name = "idx_target_id", columnList = "target_id"),
        @Index(name = "idx_audit_type", columnList = "audit_type")
})
public class AuditLog extends BaseEntity {

    /**
     * Auditor (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditor_id", nullable = false)
    private User auditor;

    /**
     * Auditor ID (admin user)
     */
    @Column(nullable = false)
    private Long auditorId;

    /**
     * Target ID (activity or fund proof)
     */
    @Column(nullable = false)
    private Long targetId;

    /**
     * Audit type
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditType auditType;

    /**
     * Audit status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus auditStatus;

    /**
     * Audit reason/comment
     */
    @Column(columnDefinition = "LONGTEXT")
    private String reason;

    /**
     * Audit type enumeration
     */
    public enum AuditType {
        ACTIVITY,           // Activity audit
        FUND_PROOF          // Fund proof audit
    }

    /**
     * Audit status enumeration
     */
    public enum AuditStatus {
        APPROVED,           // Approved
        REJECTED            // Rejected
    }

}
