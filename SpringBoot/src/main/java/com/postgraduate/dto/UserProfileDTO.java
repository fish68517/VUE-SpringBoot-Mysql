package com.postgraduate.dto;

import com.postgraduate.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for UserProfile information in API responses and requests.
 * Contains all academic profile fields for a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    private Long userId;
    private String undergradTier;
    private BigDecimal gpa;
    private String gpaScale;
    private Integer cet4Score;
    private Integer cet6Score;
    private Integer targetScore;
    private String otherScores;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserProfileDTO fromEntity(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }
        return UserProfileDTO.builder()
                .userId(userProfile.getUserId())
                .undergradTier(userProfile.getUndergradTier())
                .gpa(userProfile.getGpa())
                .gpaScale(userProfile.getGpaScale())
                .cet4Score(userProfile.getCet4Score())
                .cet6Score(userProfile.getCet6Score())
                .targetScore(userProfile.getTargetScore())
                .otherScores(userProfile.getOtherScores())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .build();
    }

}
