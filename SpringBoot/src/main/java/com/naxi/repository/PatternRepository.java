package com.naxi.repository;

import com.naxi.entity.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, Long> {
    /**
     * 按分类查询纹样
     */
    Page<Pattern> findByCategory(Pattern.PatternCategory category, Pageable pageable);

    /**
     * 按名称或描述搜索纹样
     */
    Page<Pattern> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);

    /**
     * 按名称、描述或文化背景搜索纹样
     */
    @Query("SELECT p FROM Pattern p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.culturalBackground LIKE %:keyword%")
    Page<Pattern> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 按分类和关键词搜索纹样
     */
    @Query("SELECT p FROM Pattern p WHERE p.category = :category AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.culturalBackground LIKE %:keyword%)")
    Page<Pattern> searchByCategoryAndKeyword(@Param("category") Pattern.PatternCategory category, @Param("keyword") String keyword, Pageable pageable);

    /**
     * 按应用场景搜索纹样
     */
    @Query("SELECT p FROM Pattern p WHERE p.applicationScenarios LIKE %:scenario%")
    Page<Pattern> searchByApplicationScenario(@Param("scenario") String scenario, Pageable pageable);
}
