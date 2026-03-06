package com.submission.mapper;

import com.submission.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Category Mapper - Data access layer for Category entity
 */
@Mapper
public interface CategoryMapper {

    /**
     * Insert a new category
     */
    @Insert("INSERT INTO categories (name, description, requirements, file_format, max_file_size, " +
            "word_count_min, word_count_max, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{requirements}, #{fileFormat}, #{maxFileSize}, " +
            "#{wordCountMin}, #{wordCountMax}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    /**
     * Find category by id
     */
    @Select("SELECT id, name, description, requirements, file_format, max_file_size, " +
            "word_count_min, word_count_max, created_at, updated_at " +
            "FROM categories WHERE id = #{id}")
    Category findById(Long id);

    /**
     * Find all categories
     */
    @Select("SELECT id, name, description, requirements, file_format, max_file_size, " +
            "word_count_min, word_count_max, created_at, updated_at " +
            "FROM categories ORDER BY created_at DESC")
    List<Category> findAll();

    /**
     * Update category
     */
    @Update("UPDATE categories SET name = #{name}, description = #{description}, " +
            "requirements = #{requirements}, file_format = #{fileFormat}, max_file_size = #{maxFileSize}, " +
            "word_count_min = #{wordCountMin}, word_count_max = #{wordCountMax}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Category category);

    /**
     * Delete category by id
     */
    @Delete("DELETE FROM categories WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * Find category by name
     */
    @Select("SELECT id, name, description, requirements, file_format, max_file_size, " +
            "word_count_min, word_count_max, created_at, updated_at " +
            "FROM categories WHERE name = #{name}")
    Category findByName(String name);
}
