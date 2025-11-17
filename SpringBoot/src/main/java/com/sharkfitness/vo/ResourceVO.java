package com.sharkfitness.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Value object for FitnessResource responses
 */
@Data
public class ResourceVO {
    
    private Long id;
    private String title;
    private String description;
    private String contentType;
    private String fileUrl;
    private String content;
    private Long creatorId;
    private String creatorName;
    private String creatorRole;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
