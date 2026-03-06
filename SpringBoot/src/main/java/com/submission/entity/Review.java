package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Review Entity - Represents a manuscript review by a reviewer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Long id;
    private Long manuscriptId;
    private Long reviewerId;
    private Long editorId;
    private String status; // PENDING, ACCEPTED, REJECTED, SUBMITTED
    private String opinion;
    private Integer score;
    private String recommendation; // ACCEPT, ACCEPT_WITH_REVISION, REJECT
    private LocalDateTime submittedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
