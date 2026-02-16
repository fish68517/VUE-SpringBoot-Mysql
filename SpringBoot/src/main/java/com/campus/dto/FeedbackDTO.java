package com.campus.dto;

import com.campus.entity.Feedback;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Feedback Data Transfer Object
 * Used for transferring feedback data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackDTO {

    /**
     * Feedback ID
     */
    private Long id;

    /**
     * Activity ID
     */
    private Long activityId;

    /**
     * Activity information (nested)
     */
    private ActivityDTO activity;

    /**
     * User ID
     */
    private Long userId;

    /**
     * User information (nested)
     */
    private UserDTO user;

    /**
     * Rating (1-5 stars)
     */
    private Integer rating;

    /**
     * Feedback content
     */
    private String content;

    /**
     * Reply content from organizer
     */
    private String replyContent;

    /**
     * User ID of the person who replied
     */
    private Long replyBy;

    /**
     * Replier information (nested)
     */
    private UserDTO replier;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Convert entity to DTO
     */
    public static FeedbackDTO fromEntity(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        return FeedbackDTO.builder()
                .id(feedback.getId())
                .activityId(feedback.getActivityId())
                .userId(feedback.getUserId())
                .rating(feedback.getRating())
                .content(feedback.getContent())
                .replyContent(feedback.getReplyContent())
                .replyBy(feedback.getReplyBy())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public Feedback toEntity() {
        Feedback feedback = Feedback.builder()
                .activityId(this.activityId)
                .userId(this.userId)
                .rating(this.rating)
                .content(this.content)
                .replyContent(this.replyContent)
                .replyBy(this.replyBy)
                .build();
        feedback.setId(this.id);
        return feedback;
    }

}
