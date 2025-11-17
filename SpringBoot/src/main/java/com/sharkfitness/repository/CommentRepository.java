package com.sharkfitness.repository;

import com.sharkfitness.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Comment entity
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * Find all comments for a dynamic post ordered by created_at ascending
     * @param dynamicId the dynamic ID
     * @return List of comments
     */
    List<Comment> findByDynamicIdOrderByCreatedAtAsc(Long dynamicId);
    
    /**
     * Delete all comments by a specific user
     */
    void deleteByUserId(Long userId);
}
