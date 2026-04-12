package com.travelMemory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User statistics response DTO
 * Contains aggregated statistics for a user's dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {
    
    /**
     * Total number of travel records created by the user
     */
    private Long totalRecords;
    
    /**
     * Total number of travel plans created by the user
     */
    private Long totalPlans;
    
    /**
     * Total number of likes received on user's public records
     */
    private Long totalLikesReceived;
    
    /**
     * Total number of comments received on user's public records
     */
    private Long totalCommentsReceived;
    
    /**
     * Total number of map footprints created by the user
     */
    private Long totalFootprints;
    
    /**
     * Total number of multimedia files uploaded by the user
     */
    private Long totalMultimediaFiles;
}
