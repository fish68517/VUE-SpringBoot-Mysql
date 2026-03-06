package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * InitialReview Entity - Represents the initial review of a manuscript by an editor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitialReview {
    private Long id;
    private Long manuscriptId;
    private Long editorId;
    private String status; // PASS, REJECT, REVISION_REQUIRED
    private String opinion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
