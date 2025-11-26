package com.travelMemory.service;

import com.travelMemory.dto.CreateTravelPlanRequest;
import com.travelMemory.dto.TravelPlanResponse;
import com.travelMemory.entity.TravelPlan;
import com.travelMemory.repository.TravelPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final TravelPlanRepository travelPlanRepository;

    /**
     * Create a new travel plan
     * @param userId the user ID
     * @param request the create travel plan request
     * @return TravelPlanResponse containing the created plan
     * @throws IllegalArgumentException if validation fails
     */
    public TravelPlanResponse createTravelPlan(Long userId, CreateTravelPlanRequest request) {
        // Validate dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Create new travel plan
        TravelPlan travelPlan = TravelPlan.builder()
                .userId(userId)
                .title(request.getTitle())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .budget(request.getBudget())
                .description(request.getDescription())
                .build();

        // Save to database
        TravelPlan savedPlan = travelPlanRepository.save(travelPlan);

        // Convert to response DTO
        return TravelPlanResponse.from(savedPlan);
    }

    /**
     * Get travel plans for a user with pagination
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of travel plans
     */
    @Transactional(readOnly = true)
    public Page<TravelPlanResponse> getUserTravelPlans(Long userId, Pageable pageable) {
        Page<TravelPlan> plans = travelPlanRepository.findByUserId(userId, pageable);
        return plans.map(TravelPlanResponse::from);
    }

    /**
     * Get a travel plan by ID
     * @param planId the plan ID
     * @return TravelPlanResponse
     * @throws IllegalArgumentException if plan not found
     */
    @Transactional(readOnly = true)
    public TravelPlanResponse getTravelPlanById(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found"));
        return TravelPlanResponse.from(plan);
    }

    /**
     * Get a travel plan by ID and verify ownership
     * @param planId the plan ID
     * @param userId the user ID
     * @return TravelPlanResponse
     * @throws IllegalArgumentException if plan not found or user is not the owner
     */
    @Transactional(readOnly = true)
    public TravelPlanResponse getTravelPlanByIdAndUserId(Long planId, Long userId) {
        TravelPlan plan = travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));
        return TravelPlanResponse.from(plan);
    }

    /**
     * Update a travel plan
     * @param planId the plan ID
     * @param userId the user ID (for ownership verification)
     * @param request the update travel plan request
     * @return TravelPlanResponse containing the updated plan
     * @throws IllegalArgumentException if plan not found, user is not the owner, or validation fails
     */
    public TravelPlanResponse updateTravelPlan(Long planId, Long userId, CreateTravelPlanRequest request) {
        // Verify ownership
        TravelPlan plan = travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

        // Validate dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Update fields
        plan.setTitle(request.getTitle());
        plan.setDestination(request.getDestination());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setBudget(request.getBudget());
        plan.setDescription(request.getDescription());

        // Save to database
        TravelPlan updatedPlan = travelPlanRepository.save(plan);

        // Convert to response DTO
        return TravelPlanResponse.from(updatedPlan);
    }

    /**
     * Delete a travel plan
     * @param planId the plan ID
     * @param userId the user ID (for ownership verification)
     * @throws IllegalArgumentException if plan not found or user is not the owner
     */
    public void deleteTravelPlan(Long planId, Long userId) {
        // Verify ownership
        TravelPlan plan = travelPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

        // Delete from database (cascade delete will handle related itinerary items)
        travelPlanRepository.delete(plan);
    }
}
