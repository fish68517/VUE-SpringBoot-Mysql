package com.sharkfitness.repository;

import com.sharkfitness.entity.TrainingPlan;
import com.sharkfitness.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for TrainingPlan entity
 */
@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    
    /**
     * Find all training plans by coach
     */
    List<TrainingPlan> findByCoach(User coach);
    
    /**
     * Find all training plans by coach with pagination
     */
    Page<TrainingPlan> findByCoach(User coach, Pageable pageable);
    
    /**
     * Find all training plans by coach ID
     */
    List<TrainingPlan> findByCoachId(Long coachId);
    
    /**
     * Find all training plans by student
     */
    List<TrainingPlan> findByStudent(User student);
    
    /**
     * Find all training plans by student with pagination
     */
    Page<TrainingPlan> findByStudent(User student, Pageable pageable);
    
    /**
     * Find all training plans by student ID
     */
    List<TrainingPlan> findByStudentId(Long studentId);
    
    /**
     * Find training plans by coach and student
     */
    List<TrainingPlan> findByCoachAndStudent(User coach, User student);
    
    /**
     * Find training plans by status
     */
    List<TrainingPlan> findByStatus(String status);
    
    /**
     * Count training plans by coach
     */
    Long countByCoach(User coach);
    
    /**
     * Count training plans by student
     */
    Long countByStudent(User student);
    
    /**
     * Delete all training plans created by a coach
     */
    void deleteByCoachId(Long coachId);
    
    /**
     * Delete all training plans assigned to a student
     */
    void deleteByStudentId(Long studentId);
}
