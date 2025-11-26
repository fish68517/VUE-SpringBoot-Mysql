package com.travelMemory.dto;

import com.travelMemory.entity.TravelPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanResponse {

    private Long id;

    private Long userId;

    private String title;

    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal budget;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Convert TravelPlan entity to TravelPlanResponse DTO
     * @param travelPlan the travel plan entity
     * @return TravelPlanResponse DTO
     */
    public static TravelPlanResponse from(TravelPlan travelPlan) {
        return TravelPlanResponse.builder()
                .id(travelPlan.getId())
                .userId(travelPlan.getUserId())
                .title(travelPlan.getTitle())
                .destination(travelPlan.getDestination())
                .startDate(travelPlan.getStartDate())
                .endDate(travelPlan.getEndDate())
                .budget(travelPlan.getBudget())
                .description(travelPlan.getDescription())
                .createdAt(travelPlan.getCreatedAt())
                .updatedAt(travelPlan.getUpdatedAt())
                .build();
    }
}
