package com.sharkfitness.repository;

import com.sharkfitness.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Like entity
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    /**
     * Find like by user ID and dynamic ID
     * @param userId the user ID
     * @param dynamicId the dynamic ID
     * @return Optional containing the like if found
     */
    Optional<Like> findByUserIdAndDynamicId(Long userId, Long dynamicId);
    
    /**
     * Check if user has liked a dynamic post
     * @param userId the user ID
     * @param dynamicId the dynamic ID
     * @return true if like exists, false otherwise
     */
    boolean existsByUserIdAndDynamicId(Long userId, Long dynamicId);
    
    /**
     * Delete all likes by a specific user
     */
    void deleteByUserId(Long userId);
}
