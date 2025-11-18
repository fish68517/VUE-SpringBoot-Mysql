package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.entity.Category;
import com.xingluo.petshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类控制器
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * 获取分类列表
     * @return 分类列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.getAllCategories();
        return ApiResponse.ok(categories);
    }
}
