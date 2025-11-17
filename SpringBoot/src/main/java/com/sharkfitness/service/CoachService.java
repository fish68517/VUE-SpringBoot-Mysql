package com.sharkfitness.service;

import com.sharkfitness.vo.UserVO;

import java.util.List;

/**
 * Service interface for coach operations
 */
public interface CoachService {
    
    /**
     * Add a student to a coach's roster
     * @param coachId the coach's user ID
     * @param studentId the student's user ID
     */
    void addStudent(Long coachId, Long studentId);
    
    /**
     * Remove a student from a coach's roster
     * @param coachId the coach's user ID
     * @param studentId the student's user ID
     */
    void removeStudent(Long coachId, Long studentId);
    
    /**
     * Get all students for a specific coach
     * @param coachId the coach's user ID
     * @return list of student profiles
     */
    List<UserVO> getMyStudents(Long coachId);
    
    /**
     * Get all coaches in the system
     * @return list of coach profiles
     */
    List<UserVO> getAllCoaches();
    
    /**
     * Get a specific coach's profile with additional information
     * @param coachId the coach's user ID
     * @return coach profile with student count
     */
    UserVO getCoachProfile(Long coachId);
}
