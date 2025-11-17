package com.sharkfitness.service;

import com.sharkfitness.entity.User;
import com.sharkfitness.vo.CheckInStatsVO;
import com.sharkfitness.vo.CheckInVO;

import java.util.List;

/**
 * Service interface for check-in operations
 */
public interface CheckInService {
    
    /**
     * Perform a check-in for the current user
     * @param user Current user
     * @return CheckInVO
     */
    CheckInVO performCheckIn(User user);
    
    /**
     * Get check-in history for a user
     * @param user User
     * @return List of check-ins
     */
    List<CheckInVO> getHistory(User user);
    
    /**
     * Calculate check-in statistics for a user
     * @param user User
     * @return CheckInStatsVO with statistics
     */
    CheckInStatsVO calculateStats(User user);
    
    /**
     * Get check-in history for a student (coach access)
     * @param coachId Coach's user ID
     * @param studentId Student's user ID
     * @return List of check-ins
     */
    List<CheckInVO> getStudentCheckIns(Long coachId, Long studentId);
}
