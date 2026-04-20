package com.travelMemory.service;

import com.travelMemory.dto.CreateTravelPlanRequest;
import com.travelMemory.dto.TravelPlanResponse;
import com.travelMemory.entity.TravelPlan;
import com.travelMemory.repository.TravelPlanRepository;
import com.travelMemory.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminPlanService {

    private final TravelPlanRepository travelPlanRepository;

    @Transactional(readOnly = true)
    public Page<TravelPlanResponse> getTravelPlans(Pageable pageable) {
        return travelPlanRepository.findAll(pageable).map(TravelPlanResponse::from);
    }

    @Transactional(readOnly = true)
    public TravelPlanResponse getTravelPlan(Long planId) {
        return TravelPlanResponse.from(findPlan(planId));
    }

    public TravelPlanResponse updateTravelPlan(Long planId, CreateTravelPlanRequest request) {
        TravelPlan plan = findPlan(planId);

        plan.setTitle(SecurityUtils.sanitizeText(request.getTitle()));
        plan.setDestination(SecurityUtils.sanitizeText(request.getDestination()));
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setBudget(request.getBudget());
        plan.setDescription(SecurityUtils.sanitizeText(request.getDescription()));

        return TravelPlanResponse.from(travelPlanRepository.save(plan));
    }

    public void deleteTravelPlan(Long planId) {
        travelPlanRepository.delete(findPlan(planId));
    }

    private TravelPlan findPlan(Long planId) {
        return travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("旅行计划不存在"));
    }
}
