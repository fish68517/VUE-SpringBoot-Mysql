package com.sharkfitness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating a comment
 */
@Data
public class CommentRequest {
    
    @NotNull(message = "Dynamic ID is required")
    private Long dynamicId;
    
    @NotBlank(message = "Content is required")
    @Size(max = 1000, message = "Content must not exceed 1000 characters")
    private String content;
}
