package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识数据访问层接口
 */
@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {

    /**
     * 根据分类查询知识文章列表（分页）
     *
     * @param category 分类
     * @param pageable 分页信息
     * @return 知识文章分页列表
     */
    Page<Knowledge> findByCategory(String category, Pageable pageable);

    /**
     * 根据作者查询知识文章列表
     *
     * @param author 作者名称
     * @return 知识文章列表
     */
    List<Knowledge> findByAuthor(String author);

    /**
     * 根据标题模糊查询知识文章列表（分页）
     *
     * @param title 标题关键词
     * @param pageable 分页信息
     * @return 知识文章分页列表
     */
    @Query("SELECT k FROM Knowledge k WHERE k.title LIKE %:title%")
    Page<Knowledge> searchByTitle(@Param("title") String title, Pageable pageable);

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    @Query("SELECT DISTINCT k.category FROM Knowledge k WHERE k.category IS NOT NULL")
    List<String> findAllCategories();
}
