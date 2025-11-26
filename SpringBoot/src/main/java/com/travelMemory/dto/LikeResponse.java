package com.travelMemory.dto;

import com.travelMemory.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {

    private Long id;

    private Long travelRecordId;

    private Long userId;

    private LocalDateTime createdAt;

    /**
     * Convert Like entity to LikeResponse DTO
     * @param like the like entity
     * @return LikeResponse DTO
     */
    public static LikeResponse from(Like like) {
        return LikeResponse.builder()
                .id(like.getId())
                .travelRecordId(like.getTravelRecordId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();
    }
}
