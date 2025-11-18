package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品 Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 按分类查询商品（分页）
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 商品分页列表
     */
    Page<Product> findByCategoryIdAndStatus(Long categoryId, Integer status, Pageable pageable);
    
    /**
     * 查询上架商品（分页）
     * @param status 状态
     * @param pageable 分页参数
     * @return 商品分页列表
     */
    Page<Product> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 搜索商品（按名称或描述）
     * @param keyword 关键词
     * @param status 状态
     * @param pageable 分页参数
     * @return 商品分页列表
     */
    @Query("SELECT p FROM Product p WHERE (p.name LIKE %:keyword% OR p.description LIKE %:keyword%) AND p.status = :status")
    Page<Product> searchProducts(@Param("keyword") String keyword, @Param("status") Integer status, Pageable pageable);
    
    /**
     * 根据分类ID列表查询商品（按销量排序）
     * @param categoryIds 分类ID列表
     * @param status 状态
     * @param pageable 分页参数
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.categoryId IN :categoryIds AND p.status = :status ORDER BY p.sales DESC")
    List<Product> findByCategoryIdInAndStatusOrderBySalesDesc(@Param("categoryIds") List<Long> categoryIds, @Param("status") Integer status, Pageable pageable);
    
    /**
     * 根据商品ID列表查询相同分类的其他商品（按销量排序）
     * @param productIds 商品ID列表
     * @param status 状态
     * @param pageable 分页参数
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.categoryId IN (SELECT p2.categoryId FROM Product p2 WHERE p2.id IN :productIds) AND p.id NOT IN :productIds AND p.status = :status ORDER BY p.sales DESC")
    List<Product> findSimilarProductsByIds(@Param("productIds") List<Long> productIds, @Param("status") Integer status, Pageable pageable);
}
