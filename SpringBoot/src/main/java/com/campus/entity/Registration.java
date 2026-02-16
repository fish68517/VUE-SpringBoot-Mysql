package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registration Entity
 * Represents a user's registration for an activity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registrations", indexes = {
        @Index(name = "idx_activity_id", columnList = "activity_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_unique_registration", columnList = "activity_id,user_id", unique = true)
})
public class Registration extends BaseEntity {

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
     * Participant name
     */
    @Column(length = 100)
    private String participantName;

    /**
     * Contact phone number
     */
    @Column(length = 20)
    private String contactPhone;

    /**
     * Registration remarks/notes
     */
    @Column(columnDefinition = "LONGTEXT")
    private String remarks;

    /**
     * Registration status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    /**
     * Registration status enumeration
     */
    public enum RegistrationStatus {
        REGISTERED,         // Registered
        CANCELLED           // Cancelled
    }

}
