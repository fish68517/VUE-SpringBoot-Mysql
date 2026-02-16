package com.campus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token Verify Response DTO
 * Used for returning token verification result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenVerifyResponse {

    /**
     * Whether token is valid
     */
    private Boolean valid;

    /**
     * Username extracted from token
     */
    private String username;

    /**
     * User role extracted from token
     */
    private String role;

    /**
     * Create token verify response
     */
    public static TokenVerifyResponse of(Boolean valid, String username, String role) {
        return TokenVerifyResponse.builder()
                .valid(valid)
                .username(username)
                .role(role)
                .build();
    }

}
