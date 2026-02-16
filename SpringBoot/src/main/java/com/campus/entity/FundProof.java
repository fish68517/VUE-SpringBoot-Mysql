package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fund Proof Entity
 * Represents fund usage proof submitted by organizers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fund_proofs", indexes = {
        @Index(name = "idx_activity_id", columnList = "activity_id"),
        @Index(name = "idx_organizer_id", columnList = "organizer_id"),
        @Index(name = "idx_status", columnList = "status")
})
public class FundProof extends BaseEntity {

    /**
     * Activity ID (foreign key)
     */
    @Column(nullable = false)
    private Long activityId;

    /**
     * Organizer ID (foreign key)
     */
    @Column(nullable = false)
    private Long organizerId;

    /**
     * Proof file URL
     */
    @Column(nullable = false, length = 255)
    private String fileUrl;

    /**
     * Usage description
     */
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    /**
     * Proof status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProofStatus status;

    /**
     * Rejection reason (if rejected)
     */
    @Column(columnDefinition = "LONGTEXT")
    private String rejectionReason;

    /**
     * Proof status enumeration
     */
    public enum ProofStatus {
        PENDING_AUDIT,      // Pending audit
        APPROVED,           // Approved
        REJECTED            // Rejected
    }

}
