package com.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * InitialReview Data Transfer Object - For API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitialReviewDTO {
    private Long id;
    private Long manuscriptId;
    private Long editorId;
    private String editorName;
    private String status;
    private String opinion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
