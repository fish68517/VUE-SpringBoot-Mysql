package com.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Manuscript Data Transfer Object - For API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManuscriptDTO {
    private Long id;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String title;
    private String abstractText;
    private String content;
    private String filePath;
    private String status;
    private LocalDateTime submissionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
