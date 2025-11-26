package com.travelMemory.dto;

import com.travelMemory.entity.ItineraryItem;
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
public class ItineraryItemResponse {

    private Long id;
    private Long planId;
    private LocalDate itemDate;
    private String itemType;
    private String title;
    private String description;
    private String location;
    private LocalDateTime createdAt;

    /**
     * Convert ItineraryItem entity to response DTO
     * @param item the itinerary item entity
     * @return ItineraryItemResponse
     */
    public static ItineraryItemResponse from(ItineraryItem item) {
        return ItineraryItemResponse.builder()
                .id(item.getId())
                .planId(item.getPlanId())
                .itemDate(item.getItemDate())
                .itemType(item.getItemType())
                .title(item.getTitle())
                .description(item.getDescription())
                .location(item.getLocation())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
