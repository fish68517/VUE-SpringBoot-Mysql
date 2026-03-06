package com.submission.mapper;

import com.submission.entity.Review;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * Review Mapper - Data access layer for Review entity
 */
@Mapper
public interface ReviewMapper {

    /**
     * Insert a new review
     */
    @Insert("INSERT INTO reviews (manuscript_id, reviewer_id, editor_id, status, opinion, score, recommendation, submitted_date, created_at, updated_at) " +
            "VALUES (#{manuscriptId}, #{reviewerId}, #{editorId}, #{status}, #{opinion}, #{score}, #{recommendation}, #{submittedDate}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    /**
     * Find review by id
     */
    @Select("SELECT id, manuscript_id, reviewer_id, editor_id, status, opinion, score, recommendation, submitted_date, created_at, updated_at " +
            "FROM reviews WHERE id = #{id}")
    Review findById(Long id);

    /**
     * Find all reviews by manuscript id
     */
    @Select("SELECT id, manuscript_id, reviewer_id, editor_id, status, opinion, score, recommendation, submitted_date, created_at, updated_at " +
            "FROM reviews WHERE manuscript_id = #{manuscriptId} ORDER BY created_at DESC")
    List<Review> findByManuscriptId(Long manuscriptId);

    /**
     * Find all reviews by reviewer id
     */
    @Select("SELECT id, manuscript_id, reviewer_id, editor_id, status, opinion, score, recommendation, submitted_date, created_at, updated_at " +
            "FROM reviews WHERE reviewer_id = #{reviewerId} ORDER BY created_at DESC")
    List<Review> findByReviewerId(Long reviewerId);

    /**
     * Update review
     */
    @Update("UPDATE reviews SET status = #{status}, opinion = #{opinion}, score = #{score}, recommendation = #{recommendation}, " +
            "submitted_date = #{submittedDate}, updated_at = NOW() WHERE id = #{id}")
    int update(Review review);

    /**
     * Delete review
     */
    @Delete("DELETE FROM reviews WHERE id = #{id}")
    int delete(Long id);

    /**
     * Count total review tasks
     */
    @Select("SELECT COUNT(*) FROM reviews")
    long countTotalReviewTasks();

    /**
     * Count review tasks by status
     */
    @Select("SELECT COUNT(*) FROM reviews WHERE status = #{status}")
    long countReviewTasksByStatus(String status);
}
