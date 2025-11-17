package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object for check-in statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInStatsVO {
    
    /**
     * Total number of check-ins
     */
    private Long totalCount;
    
    /**
     * Current consecutive check-in streak
     */
    private Integer currentStreak;
    
    /**
     * Longest consecutive check-in streak
     */
    private Integer longestStreak;
}
