package com.travelMemory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOverviewResponse {
    private long totalUsers;
    private long totalAdmins;
    private long totalTravelRecords;
    private long totalTravelPlans;
    private long totalComments;
    private long totalLikes;
}
