package com.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Category Data Transfer Object - For API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String requirements;
    private String fileFormat;
    private Long maxFileSize;
    private Integer wordCountMin;
    private Integer wordCountMax;
}
