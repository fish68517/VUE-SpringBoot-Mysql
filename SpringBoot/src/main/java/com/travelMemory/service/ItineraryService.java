package com.travelMemory.service;

import com.travelMemory.dto.CreateItineraryItemRequest;
import com.travelMemory.dto.ItineraryItemResponse;
import com.travelMemory.entity.ItineraryItem;
import com.travelMemory.entity.TravelPlan;
import com.travelMemory.repository.ItineraryItemRepository;
import com.travelMemory.repository.TravelPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItineraryService {

    private final ItineraryItemRepository itineraryItemRepository;
    private final TravelPlanRepository travelPlanRepository;

    /**
     * Create a new itinerary item for a travel plan
     * @param planId the plan ID
     * @param userId the user ID (for ownership verification)
     * @param request the create itinerary item request
     * @return ItineraryItemResponse containing the created item
     * @throws IllegalArgumentException if plan not found, user is not the owner, or validation fails
     */
    public ItineraryItemResponse createItineraryItem(Long planId, Long userId, CreateItineraryItemRequest request) {
        // Verify plan exists and user is the owner
        TravelPlan plan = travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

        // Validate item date is within plan dates
        if (request.getItemDate().isBefore(plan.getStartDate()) || request.getItemDate().isAfter(plan.getEndDate())) {
            throw new IllegalArgumentException("Item date must be within the plan's date range");
        }

        // Create new itinerary item
        ItineraryItem item = ItineraryItem.builder()
                .planId(planId)
                .itemDate(request.getItemDate())
                .itemType(request.getItemType())
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .build();

        // Save to database
        ItineraryItem savedItem = itineraryItemRepository.save(item);

        // Convert to response DTO
        return ItineraryItemResponse.from(savedItem);
    }

    /**
     * Get all itinerary items for a travel plan
     * @param planId the plan ID
     * @param userId the user ID (for ownership verification)
     * @return List of ItineraryItemResponse
     * @throws IllegalArgumentException if plan not found or user is not the owner
     */
    @Transactional(readOnly = true)
    public List<ItineraryItemResponse> getItineraryItems(Long planId, Long userId) {
        // Verify plan exists and user is the owner
        travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

        // Get all itinerary items for the plan
        List<ItineraryItem> items = itineraryItemRepository.findByPlanIdOrderByItemDate(planId);

        // Convert to response DTOs
        return items.stream()
                .map(ItineraryItemResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific itinerary item
     * @param itemId the item ID
     * @param planId the plan ID
     * @param userId the user ID (for ownership verification)
     * @return ItineraryItemResponse
     * @throws IllegalArgumentException if item not found, plan not found, or user is not the owner
     */
    @Transactional(readOnly = true)
    public ItineraryItemResponse getItineraryItem(Long itemId, Long planId, Long userId) {
        // Verify plan exists and user is the owner
        travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

        // Get the itinerary item
        ItineraryItem item = itineraryItemRepository.findByIdAndPlanId(itemId, planId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerary item not found"));

        // Convert to response DTO
        return ItineraryItemResponse.from(item);
    }

    /**
     * Update an itinerary item
     * @param itemId the item ID
     * @param planId the plan ID
     * @param userId the user ID (for ownership verification)
     * @param request the update itinerary item request
     * @return ItineraryItemResponse containing the updated item
     * @throws IllegalArgumentException if item not found, plan not found, user is not the owner, or validation fails
     */
    public ItineraryItemResponse updateItineraryItem(Long itemId, Long planId, Long userId, CreateItineraryItemRequest request) {
        // Verify plan exists and user is the owner
        TravelPlan plan = travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

        // Get the itinerary item
        ItineraryItem item = itineraryItemRepository.findByIdAndPlanId(itemId, planId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerary item not found"));

        // Validate item date is within plan dates
        if (request.getItemDate().isBefore(plan.getStartDate()) || request.getItemDate().isAfter(plan.getEndDate())) {
            throw new IllegalArgumentException("Item date must be within the plan's date range");
        }

        // Update fields
        item.setItemDate(request.getItemDate());
        item.setItemType(request.getItemType());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setLocation(request.getLocation());

        // Save to database
        ItineraryItem updatedItem = itineraryItemRepository.save(item);

        // Convert to response DTO
        return ItineraryItemResponse.from(updatedItem);
    }

    /**
     * Delete an itinerary item
     * @param itemId the item ID
     * @param planId the plan ID
     * @param userId the user ID (for ownership verification)
     * @throws IllegalArgumentException if item not found, plan not found, or user is not the owner
     */
    public void deleteItineraryItem(Long itemId, Long planId, Long userId) {
        // Verify plan exists and user is the owner
        travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

        // Get the itinerary item
        ItineraryItem item = itineraryItemRepository.findByIdAndPlanId(itemId, planId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerary item not found"));

        // Delete from database
        itineraryItemRepository.delete(item);
    }
}
