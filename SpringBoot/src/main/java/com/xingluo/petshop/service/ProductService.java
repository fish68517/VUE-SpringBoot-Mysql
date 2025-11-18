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
    
    /**
     * 创建商品（店家）
     * @param product 商品信息
     * @return 创建的商品
     */
    Product createProduct(Product product);
    
    /**
     * 更新商品（店家）
     * @param id 商品ID
     * @param product 商品信息
     * @return 更新后的商品
     */
    Product updateProduct(Long id, Product product);
    
    /**
     * 删除商品（店家）
     * @param id 商品ID
     * @param shopId 店铺ID
     */
    void deleteProduct(Long id, Long shopId);
    
    /**
     * 上下架商品（店家）
     * @param id 商品ID
     * @param shopId 店铺ID
     * @param status 状态（0-下架/1-上架）
     */
    void updateProductStatus(Long id, Long shopId, Integer status);
    
    /**
     * 查询店铺商品列表（店家）
     * @param shopId 店铺ID
     * @param pageable 分页参数
     * @return 商品分页列表
     */
    Page<Product> getProductsByShop(Long shopId, Pageable pageable);
    
    /**
     * 更新商品库存（店家）
     * @param id 商品ID
     * @param shopId 店铺ID
     * @param stock 新库存数量
     * @return 更新后的商品
     */
    Product updateStock(Long id, Long shopId, Integer stock);
    
    /**
     * 扣减商品库存（订单创建时调用）
     * @param productId 商品ID
     * @param quantity 扣减数量
     */
    void deductStock(Long productId, Integer quantity);
    
    /**
     * 恢复商品库存（订单取消时调用）
     * @param productId 商品ID
     * @param quantity 恢复数量
     */
    void restoreStock(Long productId, Integer quantity);
    
    /**
     * 查询待审核商品列表（管理员）
     * @param pageable 分页参数
     * @return 待审核商品分页列表
     */
    Page<Product> getPendingAuditProducts(Pageable pageable);
    
    /**
     * 审核商品（管理员）
     * @param id 商品ID
     * @param approved 是否通过（true-通过/false-拒绝）
     * @param reason 拒绝原因（可选）
     */
    void auditProduct(Long id, Boolean approved, String reason);
}
