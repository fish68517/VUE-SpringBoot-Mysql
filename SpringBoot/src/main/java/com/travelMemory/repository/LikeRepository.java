package com.travelMemory.repository;

import com.travelMemory.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * Find a like by travel record ID and user ID
     * @param travelRecordId the travel record ID
     * @param userId the user ID
     * @return Optional containing the like if found
     */
    Optional<Like> findByTravelRecordIdAndUserId(Long travelRecordId, Long userId);

    /**
     * Count likes by travel record ID
     * @param travelRecordId the travel record ID
     * @return the number of likes
     */
    long countByTravelRecordId(Long travelRecordId);

    /**
     * Check if a user has liked a travel record
     * @param travelRecordId the travel record ID
     * @param userId the user ID
     * @return true if the user has liked the record, false otherwise
     */
    boolean existsByTravelRecordIdAndUserId(Long travelRecordId, Long userId);

    /**
     * Count total likes received on a user's public travel records
     * @param userId the user ID
     * @return the total number of likes on user's public records
     */
    @Query("SELECT COUNT(l) FROM Like l JOIN TravelRecord tr ON l.travelRecordId = tr.id WHERE tr.userId = :userId AND tr.isPublic = true")
    long countLikesForUserPublicRecords(@Param("userId") Long userId);
}
