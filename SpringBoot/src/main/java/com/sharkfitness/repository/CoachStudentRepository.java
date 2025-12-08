package com.sharkfitness.repository;

import com.sharkfitness.entity.CoachStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository interface for CoachStudent entity
 */
@Repository
public interface CoachStudentRepository extends JpaRepository<CoachStudent, Long> {
    
    /**
     * Find all coach-student relationships for a specific coach
     * @param coachId the coach's user ID
     * @return list of coach-student relationships
     */
    List<CoachStudent> findByCoachId(Long coachId);
    
    /**
     * Find all coach-student relationships for a specific student
     * @param studentId the student's user ID
     * @return list of coach-student relationships
     */
    List<CoachStudent> findByStudentId(Long studentId);
    
    /**
     * Check if a coach-student relationship exists
     * @param coachId the coach's user ID
     * @param studentId the student's user ID
     * @return true if relationship exists, false otherwise
     */
    boolean existsByCoachIdAndStudentId(Long coachId, Long studentId);
    
    /**
     * Delete all relationships where user is a coach
     */
    void deleteByCoachId(Long coachId);
    
    /**
     * Delete all relationships where user is a student
     *
     *
     */
    // 添加 @Transactional 注解

    @Transactional
    void deleteByStudentId(Long studentId);

    // 获取所有教练
    List<CoachStudent> findAll();

    // 根据 coachId 和 studentId
    CoachStudent findByCoachIdAndStudentId(Long coachId, Long studentId);
}
