package com.submission.mapper;

import com.submission.entity.InitialReview;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * InitialReview Mapper - Data access layer for InitialReview entity
 */
@Mapper
public interface InitialReviewMapper {

    /**
     * Insert a new initial review
     */
    @Insert("INSERT INTO initial_reviews (manuscript_id, editor_id, status, opinion, created_at, updated_at) " +
            "VALUES (#{manuscriptId}, #{editorId}, #{status}, #{opinion}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InitialReview initialReview);

    /**
     * Find initial review by id
     */
    @Select("SELECT id, manuscript_id, editor_id, status, opinion, created_at, updated_at " +
            "FROM initial_reviews WHERE id = #{id}")
    InitialReview findById(Long id);

    /**
     * Find initial review by manuscript id
     */
    @Select("SELECT id, manuscript_id, editor_id, status, opinion, created_at, updated_at " +
            "FROM initial_reviews WHERE manuscript_id = #{manuscriptId}")
    InitialReview findByManuscriptId(Long manuscriptId);

    /**
     * Update initial review
     */
    @Update("UPDATE initial_reviews SET status = #{status}, opinion = #{opinion}, updated_at = NOW() WHERE id = #{id}")
    int update(InitialReview initialReview);

    /**
     * Delete initial review
     */
    @Delete("DELETE FROM initial_reviews WHERE id = #{id}")
    int delete(Long id);
}
