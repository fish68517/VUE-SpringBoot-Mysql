package com.sharkfitness.service;

import com.sharkfitness.vo.AnalyticsVO;

/**
 * Service interface for training progress analytics
 */
public interface AnalyticsService {
    
    /**
     * Calculate student analytics for the specified number of days
     * 
     * @param studentId The ID of the student
     * @param days Number of days to analyze (default 30)
     * @return Analytics data
     */
    AnalyticsVO calculateStudentAnalytics(Long studentId, int days);
}
