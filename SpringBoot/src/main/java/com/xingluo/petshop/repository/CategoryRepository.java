package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分类 Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根据分类名称模糊查询
     * @param keyword 关键词
     * @return 分类列表
     */
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword%")
    List<Category> findByNameContaining(@Param("keyword") String keyword);
}
