package com.sharkfitness.repository;

import com.sharkfitness.entity.FitnessResource;
import com.sharkfitness.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for FitnessResource entity
 */
@Repository
public interface ResourceRepository extends JpaRepository<FitnessResource, Long> {
    
    /**
     * Find all resources with pagination
     */
    Page<FitnessResource> findAll(Pageable pageable);
    
    /**
     * Find resources by content type with pagination
     */
    Page<FitnessResource> findByContentType(String contentType, Pageable pageable);
    
    /**
     * Find resources by creator with pagination
     */
    Page<FitnessResource> findByCreator(User creator, Pageable pageable);
    
    /**
     * Find resources by creator ID with pagination
     */
    Page<FitnessResource> findByCreatorId(Long creatorId, Pageable pageable);
    
    /**
     * Find resources by content type and creator
     */
    Page<FitnessResource> findByContentTypeAndCreator(String contentType, User creator, Pageable pageable);
    
    /**
     * Count resources by creator
     */
    Long countByCreator(User creator);
    
    /**
     * Delete all resources created by a specific user
     */
    void deleteByCreatorId(Long creatorId);
    
    /**
     * Find resources by title or description containing the search query (case-insensitive)
     * @param title the title pattern to search for
     * @param description the description pattern to search for
     * @return list of matching resources
     */
    List<FitnessResource> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
