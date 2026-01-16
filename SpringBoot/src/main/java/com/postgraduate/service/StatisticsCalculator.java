package com.postgraduate.service;

import com.postgraduate.dto.FavoriteStatsDTO;
import com.postgraduate.entity.Favorite;
import com.postgraduate.entity.UserProfile;
import com.postgraduate.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for calculating demographic statistics from favorite data.
 * Provides methods to analyze user profiles of those who favorited a school.
 */
@Slf4j
@Service
public class StatisticsCalculator {

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Calculate favorite statistics for a school based on users who favorited it.
     * Analyzes undergradTier distribution and CET-4 score distribution.
     *
     * @param favorites list of Favorite entities for a school
     * @return FavoriteStatsDTO containing demographic distributions
     */
    public FavoriteStatsDTO calculateFavoriteStats(List<Favorite> favorites) {
        log.info("Calculating favorite statistics for {} favorites", favorites.size());

        if (favorites.isEmpty()) {
            log.info("No favorites found, returning empty statistics");
            return FavoriteStatsDTO.builder()
                    .undergradTierDistribution(new HashMap<>())
                    .cet4BucketDistribution(new HashMap<>())
                    .totalFavorites(0L)
                    .hasData(false)
                    .build();
        }

        // Extract user IDs from favorites
        List<Long> userIds = favorites.stream()
                .map(Favorite::getUserId)
                .collect(Collectors.toList());

        // Fetch user profiles for these users
        List<UserProfile> userProfiles = userProfileRepository.findByUserIdIn(userIds);
        log.info("Retrieved {} user profiles for statistics calculation", userProfiles.size());

        // Calculate undergradTier distribution
        Map<String, Long> undergradTierDistribution = calculateUndergradTierDistribution(userProfiles);

        // Calculate CET-4 score bucket distribution
        Map<String, Long> cet4BucketDistribution = calculateCet4BucketDistribution(userProfiles);

        FavoriteStatsDTO stats = FavoriteStatsDTO.builder()
                .undergradTierDistribution(undergradTierDistribution)
                .cet4BucketDistribution(cet4BucketDistribution)
                .totalFavorites((long) favorites.size())
                .hasData(true)
                .build();

        log.info("Favorite statistics calculated successfully");
        return stats;
    }

    /**
     * Calculate the distribution of undergraduate tiers among user profiles.
     * Groups users by their undergradTier and counts occurrences.
     *
     * @param userProfiles list of UserProfile entities
     * @return Map with tier names as keys and counts as values
     */
    private Map<String, Long> calculateUndergradTierDistribution(List<UserProfile> userProfiles) {
        log.debug("Calculating undergradTier distribution for {} profiles", userProfiles.size());

        Map<String, Long> distribution = userProfiles.stream()
                .filter(profile -> profile.getUndergradTier() != null && !profile.getUndergradTier().isEmpty())
                .collect(Collectors.groupingBy(
                        UserProfile::getUndergradTier,
                        Collectors.counting()
                ));

        log.debug("UndergradTier distribution: {}", distribution);
        return distribution;
    }

    /**
     * Calculate the distribution of CET-4 scores in predefined buckets.
     * Buckets: <425, 425-500, 501-550, 551+
     *
     * @param userProfiles list of UserProfile entities
     * @return Map with bucket names as keys and counts as values
     */
    private Map<String, Long> calculateCet4BucketDistribution(List<UserProfile> userProfiles) {
        log.debug("Calculating CET-4 bucket distribution for {} profiles", userProfiles.size());

        Map<String, Long> distribution = new HashMap<>();
        distribution.put("<425", 0L);
        distribution.put("425-500", 0L);
        distribution.put("501-550", 0L);
        distribution.put("551+", 0L);

        for (UserProfile profile : userProfiles) {
            if (profile.getCet4Score() != null) {
                int score = profile.getCet4Score();
                if (score < 425) {
                    distribution.put("<425", distribution.get("<425") + 1);
                } else if (score <= 500) {
                    distribution.put("425-500", distribution.get("425-500") + 1);
                } else if (score <= 550) {
                    distribution.put("501-550", distribution.get("501-550") + 1);
                } else {
                    distribution.put("551+", distribution.get("551+") + 1);
                }
            }
        }

        log.debug("CET-4 bucket distribution: {}", distribution);
        return distribution;
    }
}
