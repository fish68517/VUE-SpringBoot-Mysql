package com.submission.mapper;

import com.submission.entity.Manuscript;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * Manuscript Mapper - Data access layer for Manuscript entity
 */
@Mapper
public interface ManuscriptMapper {

    /**
     * Insert a new manuscript
     */
    @Insert("INSERT INTO manuscripts (author_id, category_id, title, abstract, content, file_path, status, submission_date, created_at, updated_at) " +
            "VALUES (#{authorId}, #{categoryId}, #{title}, #{abstractText}, #{content}, #{filePath}, #{status}, #{submissionDate}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Manuscript manuscript);

    /**
     * Find manuscript by id
     */
    @Select("SELECT id, author_id, category_id, title, abstract, content, file_path, status, submission_date, created_at, updated_at " +
            "FROM manuscripts WHERE id = #{id}")
    Manuscript findById(Long id);

    /**
     * Find all manuscripts by author id
     */
    @Select("SELECT id, author_id, category_id, title, abstract, content, file_path, status, submission_date, created_at, updated_at " +
            "FROM manuscripts WHERE author_id = #{authorId} ORDER BY created_at DESC")
    List<Manuscript> findByAuthorId(Long authorId);

    /**
     * Find all manuscripts by status
     */
    @Select("SELECT id, author_id, category_id, title, abstract, content, file_path, status, submission_date, created_at, updated_at " +
            "FROM manuscripts WHERE status = #{status} ORDER BY created_at DESC")
    List<Manuscript> findByStatus(String status);

    /**
     * Find all manuscripts
     */
    @Select("SELECT id, author_id, category_id, title, abstract, content, file_path, status, submission_date, created_at, updated_at " +
            "FROM manuscripts ORDER BY created_at DESC")
    List<Manuscript> findAll();

    /**
     * Update manuscript
     */
    @Update("UPDATE manuscripts SET category_id = #{categoryId}, title = #{title}, abstract = #{abstractText}, " +
            "content = #{content}, file_path = #{filePath}, status = #{status}, submission_date = #{submissionDate}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Manuscript manuscript);

    /**
     * Delete manuscript
     */
    @Delete("DELETE FROM manuscripts WHERE id = #{id}")
    int delete(Long id);

    /**
     * Count total manuscripts
     */
    @Select("SELECT COUNT(*) FROM manuscripts WHERE status != 'DRAFT'")
    long countTotalManuscripts();

    /**
     * Count manuscripts by status
     */
    @Select("SELECT COUNT(*) FROM manuscripts WHERE status = #{status}")
    long countManuscriptsByStatus(String status);

    /**
     * Count submissions by category
     */
    @Select("<script>" +
            "SELECT c.name, COUNT(m.id) as count " +
            "FROM manuscripts m " +
            "JOIN categories c ON m.category_id = c.id " +
            "WHERE m.status != 'DRAFT' " +
            "GROUP BY c.id, c.name " +
            "ORDER BY count DESC" +
            "</script>")
    @MapKey("name")
    java.util.Map<String, Long> countSubmissionsByCategory();

    /**
     * Get approval rate by category
     */
    @Select("<script>" +
            "SELECT c.name, " +
            "ROUND(SUM(CASE WHEN m.status = 'ACCEPTED' THEN 1 ELSE 0 END) / " +
            "NULLIF(SUM(CASE WHEN m.status IN ('ACCEPTED', 'REJECTED') THEN 1 ELSE 0 END), 0) * 100, 2) as rate " +
            "FROM manuscripts m " +
            "JOIN categories c ON m.category_id = c.id " +
            "WHERE m.status IN ('ACCEPTED', 'REJECTED') " +
            "GROUP BY c.id, c.name " +
            "ORDER BY rate DESC" +
            "</script>")
    @MapKey("name")
    java.util.Map<String, Double> getApprovalRateByCategory();
}
