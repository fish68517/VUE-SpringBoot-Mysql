package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Product;
import com.tourism.service.ProductService;
import com.tourism.service.BrowsingHistoryService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 旅游商品管理控制器
 */
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {
    
    private static final Logger logger = LoggerUtil.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private BrowsingHistoryService browsingHistoryService;
    
    /**
     * 获取商品列表端点（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取商品列表请求 - 页码: " + page + ", 每页数量: " + size);
            
            Page<Product> productPage = productService.getProductList(page, size);
            
            // 构建商品响应列表
            List<Map<String, Object>> products = productPage.getContent().stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("total", productPage.getTotalElements());
            response.put("currentPage", productPage.getNumber());
            response.put("pageSize", productPage.getSize());
            response.put("totalPages", productPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取商品列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 搜索商品端点（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理搜索商品请求 - 关键词: " + keyword + ", 页码: " + page);
            
            Page<Product> productPage = productService.searchProducts(keyword, page, size);
            
            // 构建商品响应列表
            List<Map<String, Object>> products = productPage.getContent().stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("total", productPage.getTotalElements());
            response.put("currentPage", productPage.getNumber());
            response.put("pageSize", productPage.getSize());
            response.put("totalPages", productPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "搜索商品失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 按分类查询商品端点（分页）
     * @param category 分类
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/by-category")
    public ApiResponse<Map<String, Object>> getProductsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理按分类查询商品请求 - 分类: " + category + ", 页码: " + page);
            
            Page<Product> productPage = productService.getProductsByCategory(category, page, size);
            
            // 构建商品响应列表
            List<Map<String, Object>> products = productPage.getContent().stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("total", productPage.getTotalElements());
            response.put("currentPage", productPage.getNumber());
            response.put("pageSize", productPage.getSize());
            response.put("totalPages", productPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "按分类查询商品失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取广州特色商品端点（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/guangzhou-special")
    public ApiResponse<Map<String, Object>> getGuangzhouSpecialProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取广州特色商品请求 - 页码: " + page);
            
            Page<Product> productPage = productService.getGuangzhouSpecialProducts(page, size);
            
            // 构建商品响应列表
            List<Map<String, Object>> products = productPage.getContent().stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("total", productPage.getTotalElements());
            response.put("currentPage", productPage.getNumber());
            response.put("pageSize", productPage.getSize());
            response.put("totalPages", productPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取广州特色商品失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取商品详情端点
     * @param id 商品ID
     * @param userId 用户ID（可选，用于记录浏览历史）
     * @return API响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getProductDetail(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        try {
            LoggerUtil.info(logger, "处理获取商品详情请求 - ID: " + id);
            
            Product product = productService.getProductDetail(id);
            Map<String, Object> response = buildProductResponse(product);
            
            // 如果提供了用户ID，记录浏览历史
            if (userId != null && userId > 0) {
                try {
                    browsingHistoryService.recordBrowsingHistory(userId, "product", id);
                } catch (Exception e) {
                    LoggerUtil.warn(logger, "记录浏览历史失败: " + e.getMessage());
                    // 不影响主流程，继续返回商品详情
                }
            }
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取商品详情失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 创建商品端点（管理员）
     * @param request 创建请求体
     * @return API响应
     */
    @PostMapping("/admin/create")
    public ApiResponse<Map<String, Object>> createProduct(@RequestBody CreateProductRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建商品请求 - 商品名称: " + request.getName());
            
            Product product = productService.createProduct(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                request.getImageUrl(),
                request.getCategory(),
                request.getIsGuangzhouSpecial()
            );
            
            Map<String, Object> response = buildProductResponse(product);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建商品失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新商品端点（管理员）
     * @param id 商品ID
     * @param request 更新请求体
     * @return API响应
     */
    @PutMapping("/admin/{id}")
    public ApiResponse<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新商品请求 - ID: " + id);
            
            Product product = productService.updateProduct(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                request.getImageUrl(),
                request.getCategory(),
                request.getIsGuangzhouSpecial()
            );
            
            Map<String, Object> response = buildProductResponse(product);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新商品失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除商品端点（管理员）
     * @param id 商品ID
     * @return API响应
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理删除商品请求 - ID: " + id);
            
            productService.deleteProduct(id);
            
            return ApiResponse.success("商品删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除商品失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建商品响应对象
     * @param product 商品对象
     * @return 响应Map
     */
    private Map<String, Object> buildProductResponse(Product product) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", product.getId());
        response.put("name", product.getName());
        response.put("description", product.getDescription());
        response.put("price", product.getPrice());
        response.put("stock", product.getStock());
        response.put("imageUrl", product.getImageUrl());
        response.put("category", product.getCategory());
        response.put("isGuangzhouSpecial", product.getIsGuangzhouSpecial());
        response.put("createdAt", product.getCreatedAt());
        response.put("updatedAt", product.getUpdatedAt());
        
        return response;
    }
    
    /**
     * 创建商品请求体
     */
    public static class CreateProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
        private String category;
        private Boolean isGuangzhouSpecial;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Boolean getIsGuangzhouSpecial() { return isGuangzhouSpecial; }
        public void setIsGuangzhouSpecial(Boolean isGuangzhouSpecial) { this.isGuangzhouSpecial = isGuangzhouSpecial; }
    }
    
    /**
     * 更新商品请求体
     */
    public static class UpdateProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
        private String category;
        private Boolean isGuangzhouSpecial;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Boolean getIsGuangzhouSpecial() { return isGuangzhouSpecial; }
        public void setIsGuangzhouSpecial(Boolean isGuangzhouSpecial) { this.isGuangzhouSpecial = isGuangzhouSpecial; }
    }
}
