package com.tourism.service;

import com.tourism.entity.Product;
import com.tourism.exception.BusinessException;
import com.tourism.repository.ProductRepository;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * 旅游商品业务逻辑服务
 */
@Service
public class ProductService {
    
    private static final Logger logger = LoggerUtil.getLogger(ProductService.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 获取商品列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 商品分页数据
     */
    public Page<Product> getProductList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        LoggerUtil.info(logger, "获取商品列表成功 - 页码: " + page + ", 每页数量: " + size);
        return products;
    }
    
    /**
     * 根据关键词搜索商品（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 商品分页数据
     */
    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.searchByKeyword(keyword, pageable);
        
        LoggerUtil.info(logger, "搜索商品成功 - 关键词: " + keyword + ", 页码: " + page);
        return products;
    }
    
    /**
     * 根据分类查询商品（分页）
     * @param category 分类
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 商品分页数据
     */
    public Page<Product> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByCategory(category, pageable);
        
        LoggerUtil.info(logger, "按分类查询商品成功 - 分类: " + category + ", 页码: " + page);
        return products;
    }
    
    /**
     * 获取广州特色商品（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 商品分页数据
     */
    public Page<Product> getGuangzhouSpecialProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByIsGuangzhouSpecialTrue(pageable);
        
        LoggerUtil.info(logger, "获取广州特色商品成功 - 页码: " + page);
        return products;
    }
    
    /**
     * 根据ID查询商品详情
     * @param id 商品ID
     * @return 商品对象
     */
    public Product getProductDetail(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            LoggerUtil.warn(logger, "获取商品详情失败：商品不存在 - ID: " + id);
            throw new BusinessException("商品不存在");
        }
        
        LoggerUtil.info(logger, "获取商品详情成功 - ID: " + id);
        return productOptional.get();
    }
    
    /**
     * 创建商品（管理员功能）
     * @param name 商品名称
     * @param description 商品描述
     * @param price 价格
     * @param stock 库存
     * @param imageUrl 商品图片
     * @param category 分类
     * @param isGuangzhouSpecial 是否为广州特色
     * @return 创建的商品对象
     */
    public Product createProduct(String name, String description, BigDecimal price,
                                Integer stock, String imageUrl, String category,
                                Boolean isGuangzhouSpecial) {
        // 检查商品名称是否已存在
        if (productRepository.existsByName(name)) {
            LoggerUtil.warn(logger, "创建商品失败：商品名称已存在 - " + name);
            throw new BusinessException("商品名称已存在");
        }
        
        // 创建新商品
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setIsGuangzhouSpecial(isGuangzhouSpecial != null ? isGuangzhouSpecial : false);
        
        // 保存商品
        Product savedProduct = productRepository.save(product);
        LoggerUtil.info(logger, "创建商品成功 - 商品名称: " + name);
        
        return savedProduct;
    }
    
    /**
     * 更新商品信息（管理员功能）
     * @param id 商品ID
     * @param name 商品名称
     * @param description 商品描述
     * @param price 价格
     * @param stock 库存
     * @param imageUrl 商品图片
     * @param category 分类
     * @param isGuangzhouSpecial 是否为广州特色
     * @return 更新后的商品对象
     */
    public Product updateProduct(Long id, String name, String description, BigDecimal price,
                                Integer stock, String imageUrl, String category,
                                Boolean isGuangzhouSpecial) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新商品失败：商品不存在 - ID: " + id);
            throw new BusinessException("商品不存在");
        }
        
        Product product = productOptional.get();
        
        // 检查新名称是否已被其他商品使用
        if (name != null && !name.isEmpty() && !name.equals(product.getName())) {
            if (productRepository.existsByName(name)) {
                LoggerUtil.warn(logger, "更新商品失败：商品名称已存在 - " + name);
                throw new BusinessException("商品名称已存在");
            }
        }
        
        // 更新商品信息
        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (stock != null) {
            product.setStock(stock);
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            product.setImageUrl(imageUrl);
        }
        if (category != null && !category.isEmpty()) {
            product.setCategory(category);
        }
        if (isGuangzhouSpecial != null) {
            product.setIsGuangzhouSpecial(isGuangzhouSpecial);
        }
        
        Product updatedProduct = productRepository.save(product);
        LoggerUtil.info(logger, "更新商品成功 - ID: " + id);
        
        return updatedProduct;
    }
    
    /**
     * 删除商品（管理员功能）
     * @param id 商品ID
     */
    public void deleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除商品失败：商品不存在 - ID: " + id);
            throw new BusinessException("商品不存在");
        }
        
        Product product = productOptional.get();
        
        // 删除商品
        productRepository.deleteById(id);
        LoggerUtil.info(logger, "删除商品成功 - ID: " + id + ", 商品名称: " + product.getName());
    }
}
