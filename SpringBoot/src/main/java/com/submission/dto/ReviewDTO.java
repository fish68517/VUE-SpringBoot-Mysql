package com.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Review Data Transfer Object - For API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private Long manuscriptId;
    private Long reviewerId;
    private String reviewerName;
    private Long editorId;
    private String status;
    private String opinion;
    private Integer score;
    private String recommendation;
    private LocalDateTime submittedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
