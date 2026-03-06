package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * NotificationTemplate Entity - Represents notification templates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate {
    private Long id;
    private String templateName;
    private String templateContent;
    private String templateType; // SUBMISSION, REVIEW, ACCEPTANCE, REJECTION
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
