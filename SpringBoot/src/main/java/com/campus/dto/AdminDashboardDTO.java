package com.campus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin Dashboard Data Transfer Object
 * Contains key metrics for admin dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDashboardDTO {

    /**
     * Total number of users
     */
    private Long totalUsers;

    /**
     * Total number of activities
     */
    private Long totalActivities;

    /**
     * Total crowdfunding amount
     */
    private Long totalCrowdfundingAmount;

    /**
     * Average activity rating
     */
    private Double averageActivityRating;

    /**
     * Total registrations
     */
    private Long totalRegistrations;

    /**
     * Total feedback count
     */
    private Long totalFeedback;

    /**
     * Activities pending audit
     */
    private Long pendingAuditActivities;

    /**
     * Crowdfunding proofs pending audit
     */
    private Long pendingAuditCrowdfunding;

}
