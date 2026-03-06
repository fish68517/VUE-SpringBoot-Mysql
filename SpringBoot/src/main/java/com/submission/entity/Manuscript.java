package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Manuscript Entity - Represents a manuscript submission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manuscript {
    private Long id;
    private Long authorId;
    private Long categoryId;
    private String title;
    private String abstractText;
    private String content;
    private String filePath;
    private String status; // DRAFT, SUBMITTED, UNDER_REVIEW, REVISION_REQUIRED, ACCEPTED, REJECTED
    private LocalDateTime submissionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
