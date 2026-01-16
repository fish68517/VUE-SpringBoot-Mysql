package com.postgraduate.dto;

import com.postgraduate.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Feedback information in API responses.
 * Contains feedback content, type, status, and admin reply information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDTO {

    private Long id;
    private Long userId;
    private String type;
    private String content;
    private String status;
    private String adminReply;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorUsername;

    public static FeedbackDTO fromEntity(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        return FeedbackDTO.builder()
                .id(feedback.getId())
                .userId(feedback.getUserId())
                .type(feedback.getType().name())
                .content(feedback.getContent())
                .status(feedback.getStatus().name())
                .adminReply(feedback.getAdminReply())
                .repliedAt(feedback.getRepliedAt())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .authorUsername(feedback.getUser() != null ? feedback.getUser().getUsername() : null)
                .build();
    }

    public static FeedbackDTO fromEntityWithAuthor(Feedback feedback, String authorUsername) {
        if (feedback == null) {
            return null;
        }
        return FeedbackDTO.builder()
                .id(feedback.getId())
                .userId(feedback.getUserId())
                .type(feedback.getType().name())
                .content(feedback.getContent())
                .status(feedback.getStatus().name())
                .adminReply(feedback.getAdminReply())
                .repliedAt(feedback.getRepliedAt())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .authorUsername(authorUsername)
                .build();
    }
}
