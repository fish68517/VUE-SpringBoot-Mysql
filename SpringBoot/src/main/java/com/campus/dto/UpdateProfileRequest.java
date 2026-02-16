package com.campus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Update Profile Request DTO
 * Used for updating user profile information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Avatar URL
     */
    private String avatarUrl;

    /**
     * Phone number
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Phone number format is invalid")
    private String phone;

    /**
     * Gender (optional)
     */
    private String gender;

    /**
     * Birthday (optional)
     */
    private LocalDate birthday;

}
