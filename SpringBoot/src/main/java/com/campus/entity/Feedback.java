package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feedback Entity
 * Represents user feedback for an activity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedback", indexes = {
        @Index(name = "idx_activity_id", columnList = "activity_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class Feedback extends BaseEntity {

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
     * Rating (1-5 stars)
     */
    @Column(nullable = false)
    private Integer rating;

    /**
     * Feedback content
     */
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    /**
     * Reply content from organizer
     */
    @Column(columnDefinition = "LONGTEXT")
    private String replyContent;

    /**
     * Replier (many-to-one relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_by")
    private User replier;

    /**
     * User ID of the person who replied
     */
    private Long replyBy;

}
