package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * SystemConfig Entity - Represents system configuration settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
