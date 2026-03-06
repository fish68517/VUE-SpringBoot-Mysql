package com.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NotificationTemplate Data Transfer Object - For API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateDTO {
    private Long id;
    private String templateName;
    private String templateContent;
    private String templateType;
}
