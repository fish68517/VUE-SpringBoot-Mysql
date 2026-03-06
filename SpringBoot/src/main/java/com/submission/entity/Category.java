package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Category Entity - Represents a submission category
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private Long id;
    private String name;
    private String description;
    private String requirements;
    private String fileFormat;
    private Long maxFileSize;
    private Integer wordCountMin;
    private Integer wordCountMax;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
