package com.sharkfitness.service;

import com.sharkfitness.dto.TrainingPlanRequest;
import com.sharkfitness.vo.TrainingPlanVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for training plan management
 */
public interface TrainingPlanService {
    
    /**
     * Create a new training plan (Coach only)
     */
    TrainingPlanVO create(TrainingPlanRequest request, Long coachId);
    
    /**
     * Update an existing training plan (Coach only)
     */
    TrainingPlanVO update(Long id, TrainingPlanRequest request, Long currentUserId);
    
    /**
     * Delete a training plan (Coach only)
     */
    void delete(Long id, Long currentUserId);
    
    /**
     * Find all training plans by coach
     */
    List<TrainingPlanVO> findByCoach(Long coachId);
    
    /**
     * Find all training plans by student
     */
    List<TrainingPlanVO> findByStudent(Long studentId);
    
    /**
     * Find training plan by ID
     */
    TrainingPlanVO findById(Long id, Long currentUserId);
    
    /**
     * Find all training plans with role-based filtering
     */
    List<TrainingPlanVO> findAll(Long currentUserId, String userRole);
}
