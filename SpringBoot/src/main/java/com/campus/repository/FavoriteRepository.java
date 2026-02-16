package com.campus.repository;

import com.campus.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Favorite Repository
 * Provides data access methods for Favorite entity
 */
@Repository
public interface FavoriteRepository extends BaseRepository<Favorite> {

    /**
     * Find favorite by user ID and activity ID
     */
    Optional<Favorite> findByUserIdAndActivityId(Long userId, Long activityId);

    /**
     * Find all favorites by user ID
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    /**
     * Check if user has favorited an activity
     */
    boolean existsByUserIdAndActivityId(Long userId, Long activityId);

    /**
     * Delete favorite by user ID and activity ID
     */
    void deleteByUserIdAndActivityId(Long userId, Long activityId);

}
