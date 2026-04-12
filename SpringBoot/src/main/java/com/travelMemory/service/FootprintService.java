package com.travelMemory.service;

import com.travelMemory.dto.CreateMapFootprintRequest;
import com.travelMemory.dto.MapFootprintResponse;
import com.travelMemory.entity.MapFootprint;
import com.travelMemory.entity.TravelRecord;
import com.travelMemory.repository.MapFootprintRepository;
import com.travelMemory.repository.TravelRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FootprintService {

    private final MapFootprintRepository mapFootprintRepository;
    private final TravelRecordRepository travelRecordRepository;

    /**
     * Add a map footprint to a travel record
     * @param userId the user ID (for ownership verification)
     * @param request the create map footprint request
     * @return MapFootprintResponse containing the created footprint
     * @throws IllegalArgumentException if travel record not found or user is not the owner
     */
    public MapFootprintResponse addFootprint(Long userId, CreateMapFootprintRequest request) {
        // Verify that the travel record exists and belongs to the user
        TravelRecord travelRecord = travelRecordRepository.findByIdAndUserId(request.getTravelRecordId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        // Validate coordinates
        if (request.getLatitude().doubleValue() < -90 || request.getLatitude().doubleValue() > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (request.getLongitude().doubleValue() < -180 || request.getLongitude().doubleValue() > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }

        // Create new map footprint
        MapFootprint footprint = MapFootprint.builder()
                .travelRecordId(request.getTravelRecordId())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .visitDate(request.getVisitDate())
                .build();

        // Save to database
        MapFootprint savedFootprint = mapFootprintRepository.save(footprint);

        // Convert to response DTO
        return MapFootprintResponse.from(savedFootprint);
    }

    /**
     * Get all map footprints for a travel record
     * @param travelRecordId the travel record ID
     * @param userId the user ID (for ownership verification)
     * @return List of MapFootprintResponse
     * @throws IllegalArgumentException if travel record not found or user is not the owner
     */
    @Transactional(readOnly = true)
    public List<MapFootprintResponse> getFootprintsByTravelRecord(Long travelRecordId, Long userId) {
        // Verify that the travel record exists and belongs to the user
        TravelRecord travelRecord = travelRecordRepository.findByIdAndUserId(travelRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        // Get all footprints for the travel record
        List<MapFootprint> footprints = mapFootprintRepository.findByTravelRecordId(travelRecordId);

        // Convert to response DTOs
        return footprints.stream()
                .map(MapFootprintResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Delete a map footprint
     * @param footprintId the footprint ID
     * @param userId the user ID (for ownership verification)
     * @throws IllegalArgumentException if footprint not found or user is not the owner
     */
    public void deleteFootprint(Long footprintId, Long userId) {
        // Get the footprint
        MapFootprint footprint = mapFootprintRepository.findById(footprintId)
                .orElseThrow(() -> new IllegalArgumentException("Map footprint not found"));

        // Verify that the travel record belongs to the user
        TravelRecord travelRecord = travelRecordRepository.findByIdAndUserId(footprint.getTravelRecordId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Access denied: This footprint does not belong to you"));

        // Delete from database
        mapFootprintRepository.delete(footprint);
    }

    /**
     * Delete all map footprints for a travel record (called when travel record is deleted)
     * @param travelRecordId the travel record ID
     */
    public void deleteFootprintsByTravelRecord(Long travelRecordId) {
        mapFootprintRepository.deleteByTravelRecordId(travelRecordId);
    }
}
