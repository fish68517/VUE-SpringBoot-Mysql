package com.sharkfitness.repository;

import com.sharkfitness.entity.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Dynamic entity
 */
@Repository
public interface DynamicRepository extends JpaRepository<Dynamic, Long> {
    
    /**
     * Find all dynamics ordered by created_at descending with pagination
     * @param pageable pagination information
     * @return Page of dynamics
     */
    Page<Dynamic> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find dynamics by status ordered by created_at descending with pagination
     * @param status the status to filter by (approved, pending, rejected)
     * @param pageable pagination information
     * @return Page of dynamics
     */
    Page<Dynamic> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    /**
     * Delete all dynamic posts by a specific user
     */
    void deleteByUserId(Long userId);
    
    /**
     * Find dynamics by content containing the search query and status (case-insensitive)
     * @param content the content pattern to search for
     * @param status the status to filter by (approved, pending, rejected)
     * @return list of matching dynamics
     */
    List<Dynamic> findByContentContainingIgnoreCaseAndStatus(String content, String status);
}
