package com.postgraduate.repository;

import com.postgraduate.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for UserProfile entity providing database access operations.
 * Supports user profile management and demographic statistics calculation.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Find all user profiles with a specific undergraduate tier.
     * Used for demographic statistics calculation.
     *
     * @param undergradTier the undergraduate tier to filter by
     * @return List of user profiles with the specified tier
     */
    List<UserProfile> findByUndergradTier(String undergradTier);

    /**
     * Find all user profiles with CET-4 score within a specified range.
     * Used for demographic statistics calculation.
     *
     * @param minScore the minimum CET-4 score (inclusive)
     * @param maxScore the maximum CET-4 score (inclusive)
     * @return List of user profiles with CET-4 scores in the range
     */
    List<UserProfile> findByCet4ScoreBetween(Integer minScore, Integer maxScore);

    /**
     * Find all user profiles for a list of user IDs.
     * Used for statistics calculation on favorited schools.
     *
     * @param userIds list of user IDs
     * @return List of user profiles for the specified user IDs
     */
    List<UserProfile> findByUserIdIn(List<Long> userIds);

}
