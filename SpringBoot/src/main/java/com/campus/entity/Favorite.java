package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Favorite Entity
 * Represents a user's favorite activity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favorites", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_activity_id", columnList = "activity_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "unique_favorite", columnNames = {"user_id", "activity_id"})
})
public class Favorite extends BaseEntity {

    /**
     * User who favorited the activity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * User ID (foreign key to User)
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Favorited activity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    /**
     * Activity ID (foreign key to Activity)
     */
    @Column(nullable = false)
    private Long activityId;

}
