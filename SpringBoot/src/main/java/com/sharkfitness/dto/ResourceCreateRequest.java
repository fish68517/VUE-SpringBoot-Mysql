package com.sharkfitness.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for creating or updating fitness resources
 */
@Data
public class ResourceCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Content type is required")
    private String contentType;  // video, article, document
    
    private String fileUrl;
    
    private String content;  // For text articles
}
