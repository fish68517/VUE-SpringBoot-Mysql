package com.tourism.repository;

import com.tourism.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 旅游商品数据访问接口
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 根据商品名称查询商品
     * @param name 商品名称
     * @return 商品对象
     */
    Optional<Product> findByName(String name);
    
    /**
     * 根据分类查询商品
     * @param category 分类
     * @return 商品列表
     */
    List<Product> findByCategory(String category);
    
    /**
     * 查询所有广州特色商品
     * @return 广州特色商品列表
     */
    List<Product> findByIsGuangzhouSpecialTrue();
    
    /**
     * 检查商品名称是否存在
     * @param name 商品名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 按关键词搜索商品（分页）
     * @param keyword 关键词
     * @param pageable 分页信息
     * @return 商品分页数据
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 按分类查询商品（分页）
     * @param category 分类
     * @param pageable 分页信息
     * @return 商品分页数据
     */
    Page<Product> findByCategory(String category, Pageable pageable);
    
    /**
     * 查询广州特色商品（分页）
     * @param pageable 分页信息
     * @return 商品分页数据
     */
    Page<Product> findByIsGuangzhouSpecialTrue(Pageable pageable);
}
