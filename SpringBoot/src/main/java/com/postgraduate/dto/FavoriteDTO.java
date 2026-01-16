package com.postgraduate.dto;

import com.postgraduate.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Favorite information in API responses.
 * Contains favorite metadata and associated school information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDTO {

    private Long id;
    private Long userId;
    private Long schoolId;
    private LocalDateTime createdAt;
    private SchoolDTO school;

    public static FavoriteDTO fromEntity(Favorite favorite) {
        if (favorite == null) {
            return null;
        }
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .schoolId(favorite.getSchoolId())
                .createdAt(favorite.getCreatedAt())
                .school(favorite.getSchool() != null ? SchoolDTO.fromEntity(favorite.getSchool()) : null)
                .build();
    }

    public static FavoriteDTO fromEntityWithSchool(Favorite favorite, SchoolDTO schoolDTO) {
        if (favorite == null) {
            return null;
        }
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .schoolId(favorite.getSchoolId())
                .createdAt(favorite.getCreatedAt())
                .school(schoolDTO)
                .build();
    }
}
