package com.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Data Transfer Object - For API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;
    private String status;
    private String academicAchievements;
    private String workEmail;
    private String expertiseAreas;
    private String researchDirections;
}
