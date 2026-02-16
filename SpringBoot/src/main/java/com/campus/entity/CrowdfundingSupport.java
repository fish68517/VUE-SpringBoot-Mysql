package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Crowdfunding Support Entity
 * Represents a user's support for an activity's crowdfunding
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crowdfunding_supports", indexes = {
        @Index(name = "idx_activity_id", columnList = "activity_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class CrowdfundingSupport extends BaseEntity {

    /**
     * Activity (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    /**
     * Activity ID (foreign key)
     */
    @Column(nullable = false)
    private Long activityId;

    /**
     * User (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * User ID (foreign key)
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Crowdfunding tier (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id")
    private CrowdfundingTier tier;

    /**
     * Support amount
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Crowdfunding tier ID
     */
    private Long tierId;

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    /**
     * Payment status enumeration
     */
    public enum PaymentStatus {
        PENDING,            // Pending payment
        COMPLETED,          // Payment completed
        FAILED              // Payment failed
    }

}
