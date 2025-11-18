package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class RecommendController {
    
    private final ProductService productService;
    
    /**
     * 获取推荐商品
     * @param userId 用户ID
     * @param limit 推荐数量（默认10）
     * @return 推荐商品列表
     */
    @GetMapping("/recommend")
    public ApiResponse<List<Product>> getRecommendedProducts(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Product> products = productService.getRecommendedProducts(userId, limit);
        return ApiResponse.ok(products);
    }
}
