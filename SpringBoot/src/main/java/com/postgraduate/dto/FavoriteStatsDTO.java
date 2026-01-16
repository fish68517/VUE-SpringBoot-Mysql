package com.postgraduate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for favorite statistics information in API responses.
 * Contains demographic distribution data for users who favorited a school.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteStatsDTO {

    /**
     * Distribution of undergraduate tier among users who favorited the school.
     * Key: tier name (985, 211, double-non, other)
     * Value: count of users with that tier
     */
    private Map<String, Long> undergradTierDistribution;

    /**
     * Distribution of CET-4 scores in buckets among users who favorited the school.
     * Key: bucket name (<425, 425-500, 501-550, 551+)
     * Value: count of users in that bucket
     */
    private Map<String, Long> cet4BucketDistribution;

    /**
     * Total number of users who have favorited the school.
     */
    private Long totalFavorites;

    /**
     * Indicates whether there is sufficient data for statistics.
     * Returns false when no users have favorited the school.
     */
    private Boolean hasData;
}
