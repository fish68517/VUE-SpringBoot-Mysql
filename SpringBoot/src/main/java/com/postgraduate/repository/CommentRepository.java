package com.postgraduate.repository;

import com.postgraduate.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Comment entity providing database access operations.
 * Supports comment management, retrieval, and filtering by school and status.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find all comments for a specific school with NORMAL status, excluding soft-deleted records.
     *
     * @param schoolId the school ID
     * @param pageable pagination information
     * @return Page of comments for the school
     */
    Page<Comment> findBySchoolIdAndStatusAndDeletedFalse(Long schoolId, Comment.CommentStatus status, Pageable pageable);

    /**
     * Find a comment by ID, excluding soft-deleted records.
     *
     * @param id the comment ID
     * @return Optional containing the comment if found
     */
    Optional<Comment> findByIdAndDeletedFalse(Long id);

    /**
     * Check if a comment exists by ID and is not soft-deleted.
     *
     * @param id the comment ID
     * @return true if the comment exists and is not deleted, false otherwise
     */
    boolean existsByIdAndDeletedFalse(Long id);
}
