package com.sharkfitness.repository;

import com.sharkfitness.entity.Collection;
import com.sharkfitness.entity.FitnessResource;
import com.sharkfitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Collection entity
 */
@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    
    /**
     * Find all collections for a user
     */
    List<Collection> findByUser(User user);
    
    /**
     * Find all collections for a user by user ID
     */
    List<Collection> findByUserId(Long userId);
    
    /**
     * Find a specific collection by user and resource
     */
    Optional<Collection> findByUserAndResource(User user, FitnessResource resource);
    
    /**
     * Find a specific collection by user ID and resource ID
     */
    Optional<Collection> findByUserIdAndResourceId(Long userId, Long resourceId);
    
    /**
     * Check if a collection exists for user and resource
     */
    boolean existsByUserAndResource(User user, FitnessResource resource);
    
    /**
     * Check if a collection exists by user ID and resource ID
     */
    boolean existsByUserIdAndResourceId(Long userId, Long resourceId);
    
    /**
     * Delete collection by user and resource
     */
    void deleteByUserAndResource(User user, FitnessResource resource);
    
    /**
     * Delete collection by user ID and resource ID
     */
    void deleteByUserIdAndResourceId(Long userId, Long resourceId);
    
    /**
     * Delete all collections for a specific user
     */
    void deleteByUserId(Long userId);
}
