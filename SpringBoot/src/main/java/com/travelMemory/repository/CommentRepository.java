package com.travelMemory.repository;

import com.travelMemory.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find comments by travel record ID with pagination
     * @param travelRecordId the travel record ID
     * @param pageable pagination information
     * @return Page of comments
     */
    Page<Comment> findByTravelRecordId(Long travelRecordId, Pageable pageable);

    /**
     * Find a comment by ID and user ID (for ownership verification)
     * @param id the comment ID
     * @param userId the user ID
     * @return Optional containing the comment if found
     */
    Optional<Comment> findByIdAndUserId(Long id, Long userId);

    /**
     * Count comments by travel record ID
     * @param travelRecordId the travel record ID
     * @return the number of comments
     */
    long countByTravelRecordId(Long travelRecordId);

    /**
     * Count total comments received on a user's public travel records
     * @param userId the user ID
     * @return the total number of comments on user's public records
     */
    @Query("SELECT COUNT(c) FROM Comment c JOIN TravelRecord tr ON c.travelRecordId = tr.id WHERE tr.userId = :userId AND tr.isPublic = true")
    long countCommentsForUserPublicRecords(@Param("userId") Long userId);
}
