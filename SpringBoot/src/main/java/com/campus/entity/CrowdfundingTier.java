package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Crowdfunding Tier Entity
 * Represents a crowdfunding tier for an activity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crowdfunding_tiers", indexes = {
        @Index(name = "idx_activity_id", columnList = "activity_id")
})
public class CrowdfundingTier extends BaseEntity {

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
     * Tier name (e.g., "10元档", "50元档", "100元档")
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Tier amount
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Tier description
     */
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    /**
     * Whether this is a preset tier (true) or custom tier (false)
     */
    @Column(nullable = false)
    private Boolean isPreset;

    /**
     * Display order
     */
    @Column(nullable = false)
    private Integer displayOrder;

}
