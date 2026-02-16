package com.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Request DTO
 * Used for user login requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Username (student ID)
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Password
     */
    @NotBlank(message = "Password is required")
    private String password;

}
