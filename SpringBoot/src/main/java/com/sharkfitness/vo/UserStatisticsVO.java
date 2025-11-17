package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VO for system-wide user statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsVO {
    
    private Long totalUsers;
    private Long totalRegularUsers;
    private Long totalCoaches;
    private Long totalAdmins;
    private Long totalResources;
    private Long totalDynamicPosts;
    private Long totalTrainingPlans;
}
