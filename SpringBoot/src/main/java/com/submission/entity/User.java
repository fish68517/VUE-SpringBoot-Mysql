package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User Entity - Represents a user in the system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role; // AUTHOR, EDITOR, REVIEWER, ADMIN
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, INACTIVE
    private String academicAchievements; // For authors
    private String workEmail; // For editors
    private String expertiseAreas; // For reviewers
    private String researchDirections; // For reviewers
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
