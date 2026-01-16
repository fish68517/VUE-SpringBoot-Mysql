package com.postgraduate.repository;

import com.postgraduate.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Feedback entity providing database access operations.
 * Supports feedback management, retrieval, and filtering by user, type, and status.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Find all feedback submitted by a specific user, excluding soft-deleted records.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of feedback for the user
     */
    Page<Feedback> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    /**
     * Find a feedback by ID, excluding soft-deleted records.
     *
     * @param id the feedback ID
     * @return Optional containing the feedback if found
     */
    Optional<Feedback> findByIdAndDeletedFalse(Long id);

    /**
     * Check if feedback exists by ID and is not soft-deleted.
     *
     * @param id the feedback ID
     * @return true if the feedback exists and is not deleted, false otherwise
     */
    boolean existsByIdAndDeletedFalse(Long id);

    /**
     * Find all feedback with optional filtering by status and type, excluding soft-deleted records.
     * Used by admin for feedback management.
     *
     * @param status the feedback status (can be null for no filtering)
     * @param type the feedback type (can be null for no filtering)
     * @param pageable pagination information
     * @return Page of feedback matching the criteria
     */
    @Query("SELECT f FROM Feedback f WHERE f.deleted = false " +
            "AND (:status IS NULL OR f.status = :status) " +
            "AND (:type IS NULL OR f.type = :type)")
    Page<Feedback> findAllWithFilters(
            @Param("status") Feedback.FeedbackStatus status,
            @Param("type") Feedback.FeedbackType type,
            Pageable pageable);
}
