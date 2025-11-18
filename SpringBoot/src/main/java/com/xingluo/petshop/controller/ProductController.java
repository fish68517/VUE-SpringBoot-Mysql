package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.service.BrowseHistoryService;
import com.xingluo.petshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器（用户端）
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final BrowseHistoryService browseHistoryService;
    
    /**
     * 获取商品列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段（默认：createTime）
     * @param sortDir 排序方向（asc/desc，默认：desc）
     * @return 商品分页列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<Product>> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Product> products = productService.getProductList(pageable);
        return ApiResponse.ok(products);
    }
    
    /**
     * 获取商品详情
     * @param id 商品ID
     * @param userId 用户ID（可选，用于记录浏览历史）
     * @return 商品详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Product> getProductById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        
        Product product = productService.getProductById(id);
        
        // 如果提供了用户ID，记录浏览历史
        if (userId != null) {
            try {
                browseHistoryService.recordBrowseHistory(userId, id);
            } catch (Exception e) {
                // 浏览历史记录失败不影响商品详情查询
                // 可以记录日志，但不抛出异常
            }
        }
        
        return ApiResponse.ok(product);
    }
    
    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 商品分页列表
     */
    @GetMapping("/search")
    public ApiResponse<Page<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Product> products = productService.searchProducts(keyword, pageable);
        return ApiResponse.ok(products);
    }
    
    /**
     * 按分类查询商品
     * @param id 分类ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 商品分页列表
     */
    @GetMapping("/category/{id}")
    public ApiResponse<Page<Product>> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Product> products = productService.getProductsByCategory(id, pageable);
        return ApiResponse.ok(products);
    }
}
