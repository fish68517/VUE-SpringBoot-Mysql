package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 商品分页查询
     * @param pageable 分页参数
     * @return 商品分页列表
     */
    Page<Product> getProductList(Pageable pageable);
    
    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    Product getProductById(Long id);
    
    /**
     * 按分类查询商品
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 商品分页列表
     */
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 搜索商品（按名称或描述）
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 商品分页列表
     */
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    /**
     * 基于宠物种类推荐商品
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<Product> recommendByPetSpecies(Long userId, int limit);
    
    /**
     * 基于浏览历史推荐商品
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<Product> recommendByBrowseHistory(Long userId, int limit);
    
    /**
     * 获取综合推荐商品
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<Product> getRecommendedProducts(Long userId, int limit);
}
