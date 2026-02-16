package com.campus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Crowdfunding Statistics Data Transfer Object
 * Contains aggregated statistics for organizer's crowdfunding activities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrowdfundingStatisticsDTO {

    /**
     * Total crowdfunding amount raised
     */
    private Long totalCrowdfundingAmount;

    /**
     * Total target amount for all crowdfunding activities
     */
    private Long totalTargetAmount;

    /**
     * Crowdfunding completion rate (percentage)
     */
    private Double completionRate;

    /**
     * Total number of supporters
     */
    private Long totalSupporters;

    /**
     * Average supporters per activity
     */
    private Double averageSupportPerActivity;

}
