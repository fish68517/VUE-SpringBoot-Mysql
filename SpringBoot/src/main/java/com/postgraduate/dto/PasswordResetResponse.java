package com.postgraduate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password reset response.
 * Contains the temporary password generated for a user after password reset.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetResponse {

    private String tempPassword;
    private String message;

}
