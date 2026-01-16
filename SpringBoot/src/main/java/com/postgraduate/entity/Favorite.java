package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Favorite entity representing a user's favorited school.
 * Maintains a many-to-many relationship between users and schools through favorites.
 * Each user can favorite each school at most once (enforced by unique constraint).
 */
@Entity
@Table(name = "favorites", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_school_id", columnList = "school_id"),
        @Index(name = "idx_deleted", columnList = "deleted")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_school", columnNames = {"user_id", "school_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long schoolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;
}
