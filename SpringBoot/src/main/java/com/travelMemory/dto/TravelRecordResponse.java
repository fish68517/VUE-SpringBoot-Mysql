package com.travelMemory.dto;

import com.travelMemory.entity.TravelRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelRecordResponse {

    private Long id;

    private Long userId;

    private String title;

    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private String diaryContent;

    private Boolean isPublic;

    private Integer viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserInfo user;

    private Integer likeCount;

    private Integer commentCount;

    /**
     * Convert TravelRecord entity to TravelRecordResponse DTO
     * @param travelRecord the travel record entity
     * @return TravelRecordResponse DTO
     */
    public static TravelRecordResponse from(TravelRecord travelRecord) {
        return TravelRecordResponse.builder()
                .id(travelRecord.getId())
                .userId(travelRecord.getUserId())
                .title(travelRecord.getTitle())
                .destination(travelRecord.getDestination())
                .startDate(travelRecord.getStartDate())
                .endDate(travelRecord.getEndDate())
                .description(travelRecord.getDescription())
                .diaryContent(travelRecord.getDiaryContent())
                .isPublic(travelRecord.getIsPublic())
                .viewCount(travelRecord.getViewCount())
                .createdAt(travelRecord.getCreatedAt())
                .updatedAt(travelRecord.getUpdatedAt())
                .likeCount(0)
                .commentCount(0)
                .build();
    }

    /**
     * Convert TravelRecord entity to TravelRecordResponse DTO with user info
     * @param travelRecord the travel record entity
     * @param user the user info
     * @return TravelRecordResponse DTO
     */
    public static TravelRecordResponse from(TravelRecord travelRecord, UserInfo user) {
        return TravelRecordResponse.builder()
                .id(travelRecord.getId())
                .userId(travelRecord.getUserId())
                .title(travelRecord.getTitle())
                .destination(travelRecord.getDestination())
                .startDate(travelRecord.getStartDate())
                .endDate(travelRecord.getEndDate())
                .description(travelRecord.getDescription())
                .diaryContent(travelRecord.getDiaryContent())
                .isPublic(travelRecord.getIsPublic())
                .viewCount(travelRecord.getViewCount())
                .createdAt(travelRecord.getCreatedAt())
                .updatedAt(travelRecord.getUpdatedAt())
                .user(user)
                .likeCount(0)
                .commentCount(0)
                .build();
    }
}
