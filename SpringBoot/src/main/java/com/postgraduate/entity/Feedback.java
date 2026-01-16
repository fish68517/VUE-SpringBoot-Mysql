package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Feedback entity representing user-submitted feedback about bugs, suggestions, or data errors.
 * Tracks feedback status and admin replies for issue resolution.
 */
@Entity
@Table(name = "feedback", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FeedbackType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;

    @Column(columnDefinition = "TEXT")
    private String adminReply;

    @Column(nullable = true)
    private LocalDateTime repliedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public enum FeedbackType {
        BUG, SUGGESTION, DATA_ERROR
    }

    public enum FeedbackStatus {
        NEW, PROCESSING, DONE
    }
}
