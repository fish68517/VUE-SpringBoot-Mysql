package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comment entity representing a user's comment or discussion post about a school.
 * Supports nested comments (replies) through self-referencing parent_id relationship.
 * Comments can be soft-deleted by marking status as DELETED.
 */
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_school_id", columnList = "school_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Column(nullable = false)
    private Long schoolId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true)
    private Long parentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Comment parentComment;

    public enum CommentStatus {
        NORMAL, DELETED
    }
}
