package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Activity Entity
 * Represents a campus activity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activities", indexes = {
        @Index(name = "idx_organizer_id", columnList = "organizer_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_start_time", columnList = "start_time")
})
public class Activity extends BaseEntity {

    /**
     * Organizer (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    /**
     * Organizer ID (foreign key to User)
     */
    @Column(nullable = false)
    private Long organizerId;

    /**
     * Activity title
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Activity description
     */
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    /**
     * Activity type (lecture, competition, charity, etc.)
     */
    @Column(length = 50)
    private String type;

    /**
     * Cover image URL
     */
    @Column(length = 255)
    private String coverUrl;

    /**
     * Activity start time
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * Activity end time
     */
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * Activity location
     */
    @Column(length = 255)
    private String location;

    /**
     * Maximum number of participants
     */
    private Integer maxParticipants;

    /**
     * Registration deadline
     */
    private LocalDateTime registrationDeadline;

    /**
     * Whether crowdfunding is enabled
     */
    @Column(nullable = false)
    private Boolean enableCrowdfunding;

    /**
     * Crowdfunding target amount
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal crowdfundingTarget;

    /**
     * Activity status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    /**
     * Registrations for this activity (one-to-many relationship)
     */
    @OneToMany(mappedBy = "activityId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Registration> registrations;

    /**
     * Crowdfunding supports for this activity (one-to-many relationship)
     */
    @OneToMany(mappedBy = "activityId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CrowdfundingSupport> crowdfundingSupports;

    /**
     * Feedback for this activity (one-to-many relationship)
     */
    @OneToMany(mappedBy = "activityId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    /**
     * Crowdfunding tiers for this activity (one-to-many relationship)
     */
    @OneToMany(mappedBy = "activityId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CrowdfundingTier> crowdfundingTiers;

    /**
     * Activity status enumeration
     */
    public enum ActivityStatus {
        DRAFT,              // Draft
        PENDING_AUDIT,      // Pending audit
        APPROVED,           // Approved
        REJECTED,           // Rejected
        ONGOING,            // Ongoing
        ENDED,              // Ended
        ARCHIVED            // Archived
    }

}
