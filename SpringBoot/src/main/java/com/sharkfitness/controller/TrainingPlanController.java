package com.sharkfitness.controller;

import com.sharkfitness.dto.TrainingPlanRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.service.TrainingPlanService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.TrainingPlanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for training plan management
 */
@RestController
@RequestMapping("/api/training-plans")
@RequiredArgsConstructor
public class TrainingPlanController {
    
    private final TrainingPlanService trainingPlanService;
    
    /**
     * Get all training plans with role-based filtering
     * - Coaches see their created plans
     * - Students see their assigned plans
     * - Admins see all plans
     */
    @GetMapping
    public ApiResponse<List<TrainingPlanVO>> getAllTrainingPlans(
            @RequestAttribute("currentUser") User currentUser) {
        
        List<TrainingPlanVO> plans = trainingPlanService.findAll(
                currentUser.getId(), 
                currentUser.getRole());
        return ApiResponse.success(plans);
    }
    
    /**
     * Get training plan by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<TrainingPlanVO> getTrainingPlanById(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        
        TrainingPlanVO plan = trainingPlanService.findById(id, currentUser.getId());
        return ApiResponse.success(plan);
    }
    
    /**
     * Get training plans by coach ID
     */
    @GetMapping("/coach/{coachId}")
    public ApiResponse<List<TrainingPlanVO>> getTrainingPlansByCoach(
            @PathVariable Long coachId) {
        
        List<TrainingPlanVO> plans = trainingPlanService.findByCoach(coachId);
        return ApiResponse.success(plans);
    }
    
    /**
     * Get training plans by student ID
     */
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<TrainingPlanVO>> getTrainingPlansByStudent(
            @PathVariable Long studentId) {
        
        List<TrainingPlanVO> plans = trainingPlanService.findByStudent(studentId);
        return ApiResponse.success(plans);
    }
    
    /**
     * Create a new training plan (Coach only)
     */
    @PostMapping
    public ApiResponse<TrainingPlanVO> createTrainingPlan(
            @Valid @RequestBody TrainingPlanRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        TrainingPlanVO plan = trainingPlanService.create(request, currentUser.getId());
        return ApiResponse.success(plan);
    }

    @PostMapping("/coachAndStudent")
    public ApiResponse<TrainingPlanVO> createTrainingPlanqq(
            @Valid @RequestBody TrainingPlanRequest request) {

        TrainingPlanVO plan = trainingPlanService.create(request, request.getCoachId());
        return ApiResponse.success(plan);
    }
    
    /**
     * Update a training plan (Coach only - creator)
     */
    @PutMapping("/{id}")
    public ApiResponse<TrainingPlanVO> updateTrainingPlan(
            @PathVariable Long id,
            @Valid @RequestBody TrainingPlanRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        TrainingPlanVO plan = trainingPlanService.update(id, request, currentUser.getId());
        return ApiResponse.success(plan);
    }
    
    /**
     * Delete a training plan (Coach only - creator)
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTrainingPlan(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        
        trainingPlanService.delete(id, currentUser.getId());
        return ApiResponse.success(null);
    }
}
