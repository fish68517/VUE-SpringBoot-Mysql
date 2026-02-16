package com.campus.dto;

import com.campus.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response DTO
 * Used for returning login response with JWT token
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    /**
     * JWT access token
     */
    private String accessToken;

    /**
     * Token type (Bearer)
     */
    private String tokenType;

    /**
     * Token expiration time in milliseconds
     */
    private Long expiresIn;

    /**
     * User information
     */
    private UserDTO user;

    /**
     * Create login response with token and user info
     */
    public static LoginResponse of(String token, Long expiresIn, User user) {
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(UserDTO.fromEntity(user))
                .build();
    }

}
