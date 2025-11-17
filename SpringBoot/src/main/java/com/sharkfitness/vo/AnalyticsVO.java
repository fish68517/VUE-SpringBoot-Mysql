package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VO for student training progress analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsVO {
    
    /**
     * Check-in frequency (percentage) for the specified period
     */
    private Double checkInFrequency;
    
    /**
     * Total number of check-ins in the specified period
     */
    private Long totalCheckIns;
    
    /**
     * Average daily calories from diet records
     */
    private Double averageCalories;
    
    /**
     * Number of active days (days with check-ins or diet records)
     */
    private Integer activeDays;
    
    /**
     * Training plan completion rate (percentage)
     */
    private Double planCompletionRate;
}
