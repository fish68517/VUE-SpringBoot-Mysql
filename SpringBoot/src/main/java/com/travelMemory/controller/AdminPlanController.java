package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CreateTravelPlanRequest;
import com.travelMemory.dto.TravelPlanResponse;
import com.travelMemory.service.AdminPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlanController {

    private final AdminPlanService adminPlanService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TravelPlanResponse>>> getTravelPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TravelPlanResponse> plans = adminPlanService.getTravelPlans(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelPlanResponse>> getTravelPlan(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminPlanService.getTravelPlan(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelPlanResponse>> updateTravelPlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateTravelPlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminPlanService.updateTravelPlan(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTravelPlan(@PathVariable Long id) {
        adminPlanService.deleteTravelPlan(id);
        return ResponseEntity.ok(ApiResponse.success("旅行计划删除成功", null));
    }
}
