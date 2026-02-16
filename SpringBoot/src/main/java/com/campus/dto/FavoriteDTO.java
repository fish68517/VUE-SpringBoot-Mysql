package com.campus.dto;

import com.campus.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Favorite Data Transfer Object
 * Used for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {

    /**
     * Favorite ID
     */
    private Long id;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Activity ID
     */
    private Long activityId;

    /**
     * Activity details (for list responses)
     */
    private ActivityDTO activity;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Convert Favorite entity to DTO
     */
    public static FavoriteDTO fromEntity(Favorite favorite) {
        if (favorite == null) {
            return null;
        }

        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .activityId(favorite.getActivityId())
                .activity(favorite.getActivity() != null ? ActivityDTO.fromEntity(favorite.getActivity()) : null)
                .createdAt(favorite.getCreatedAt())
                .build();
    }

}
