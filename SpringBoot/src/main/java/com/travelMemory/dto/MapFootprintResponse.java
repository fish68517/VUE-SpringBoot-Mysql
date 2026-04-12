package com.travelMemory.dto;

import com.travelMemory.entity.MapFootprint;
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
public class MapFootprintResponse {

    private Long id;
    private Long travelRecordId;
    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate visitDate;
    private LocalDateTime createdAt;

    /**
     * Convert MapFootprint entity to response DTO
     * @param footprint the map footprint entity
     * @return MapFootprintResponse
     */
    public static MapFootprintResponse from(MapFootprint footprint) {
        return MapFootprintResponse.builder()
                .id(footprint.getId())
                .travelRecordId(footprint.getTravelRecordId())
                .locationName(footprint.getLocationName())
                .latitude(footprint.getLatitude())
                .longitude(footprint.getLongitude())
                .visitDate(footprint.getVisitDate())
                .createdAt(footprint.getCreatedAt())
                .build();
    }
}
