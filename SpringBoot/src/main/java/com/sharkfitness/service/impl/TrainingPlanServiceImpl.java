package com.sharkfitness.service.impl;

import com.sharkfitness.dto.TrainingPlanRequest;
import com.sharkfitness.entity.TrainingPlan;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.exception.UnauthorizedException;
import com.sharkfitness.repository.TrainingPlanRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.TrainingPlanService;
import com.sharkfitness.vo.TrainingPlanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TrainingPlanService
 */
@Service
@RequiredArgsConstructor
public class TrainingPlanServiceImpl implements TrainingPlanService {
    
    private final TrainingPlanRepository trainingPlanRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public TrainingPlanVO create(TrainingPlanRequest request, Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
        
        // Check if user is a coach
        if (!"coach".equals(coach.getRole())) {
            throw new UnauthorizedException("Only coaches can create training plans");
        }
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        TrainingPlan plan = new TrainingPlan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setExercises(request.getExercises());
        plan.setCoach(coach);
        plan.setStudent(student);
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        
        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        }
        
        TrainingPlan saved = trainingPlanRepository.save(plan);
        return convertToVO(saved);
    }
    
    @Override
    @Transactional
    public TrainingPlanVO update(Long id, TrainingPlanRequest request, Long currentUserId) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if user is the coach who created this plan
        if (!plan.getCoach().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You don't have permission to update this training plan");
        }
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setExercises(request.getExercises());
        plan.setStudent(student);
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        
        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        }
        
        TrainingPlan updated = trainingPlanRepository.save(plan);
        return convertToVO(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id, Long currentUserId) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        
        // Check if user is the coach who created this plan
        if (!plan.getCoach().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You don't have permission to delete this training plan");
        }
        
        trainingPlanRepository.delete(plan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlanVO> findByCoach(Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
        
        return trainingPlanRepository.findByCoach(coach).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlanVO> findByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        return trainingPlanRepository.findByStudent(student).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrainingPlanVO findById(Long id, Long currentUserId) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        
        // Check if user has permission to view this plan (coach or student)
        if (!plan.getCoach().getId().equals(currentUserId) && 
            !plan.getStudent().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You don't have permission to view this training plan");
        }
        
        return convertToVO(plan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlanVO> findAll(Long currentUserId, String userRole) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<TrainingPlan> plans;
        
        // Role-based filtering
        if ("coach".equals(userRole)) {
            // Coaches see their created plans
            plans = trainingPlanRepository.findByCoach(currentUser);
        } else if ("user".equals(userRole)) {
            // Students see their assigned plans
            plans = trainingPlanRepository.findByStudent(currentUser);
        } else if ("admin".equals(userRole)) {
            // Admins see all plans
            plans = trainingPlanRepository.findAll();
        } else {
            throw new UnauthorizedException("Invalid user role");
        }
        
        return plans.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    private TrainingPlanVO convertToVO(TrainingPlan plan) {
        TrainingPlanVO vo = new TrainingPlanVO();
        vo.setId(plan.getId());
        vo.setName(plan.getName());
        vo.setDescription(plan.getDescription());
        vo.setExercises(plan.getExercises());
        vo.setStartDate(plan.getStartDate());
        vo.setEndDate(plan.getEndDate());
        vo.setStatus(plan.getStatus());
        vo.setCreatedAt(plan.getCreatedAt());
        vo.setUpdatedAt(plan.getUpdatedAt());
        
        if (plan.getCoach() != null) {
            vo.setCoachId(plan.getCoach().getId());
            vo.setCoachName(plan.getCoach().getUsername());
        }
        
        if (plan.getStudent() != null) {
            vo.setStudentId(plan.getStudent().getId());
            vo.setStudentName(plan.getStudent().getUsername());
        }
        
        return vo;
    }
}
