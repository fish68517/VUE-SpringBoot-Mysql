package com.sharkfitness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating a dynamic post
 */
@Data
public class DynamicCreateRequest {
    
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;
    
    @Size(max = 1000, message = "Image URLs must not exceed 1000 characters")
    private String imageUrls;  // Comma-separated URLs
}
