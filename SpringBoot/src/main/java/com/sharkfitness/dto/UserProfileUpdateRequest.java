package com.sharkfitness.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for user profile update request
 */
@Data
public class UserProfileUpdateRequest {
    
    @Size(max = 255, message = "Avatar URL must not exceed 255 characters")
    private String avatar;
    
    @Size(max = 10, message = "Gender must not exceed 10 characters")
    private String gender;
    
    @Size(max = 500, message = "Introduction must not exceed 500 characters")
    private String intro;
}
