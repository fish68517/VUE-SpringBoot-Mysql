package com.campus.repository;

import com.campus.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Feedback Repository
 * Provides data access methods for Feedback entity
 */
@Repository
public interface FeedbackRepository extends BaseRepository<Feedback> {

    /**
     * Find feedback by activity ID
     */
    Page<Feedback> findByActivityId(Long activityId, Pageable pageable);

    /**
     * Find feedback by user ID
     */
    Page<Feedback> findByUserId(Long userId, Pageable pageable);

    /**
     * Get average rating for an activity
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.activityId = :activityId")
    Double getAverageRatingByActivityId(@Param("activityId") Long activityId);

    /**
     * Count feedback for an activity
     */
    long countByActivityId(Long activityId);

    /**
     * Get average rating across all activities
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getAverageRating();

}
