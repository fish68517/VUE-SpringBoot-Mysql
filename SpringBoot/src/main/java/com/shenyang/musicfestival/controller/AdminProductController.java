package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.entity.ProductCategory;
import com.shenyang.musicfestival.repository.ProductCategoryRepository;
import com.shenyang.musicfestival.repository.ProductRepository;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    // ==========================================
    // 1. 商品分类管理 (Categories)
    // 解决报错 1：/api/admin/product/categories
    // ==========================================

    @GetMapping("/product/categories")
    public ResponseEntity<ApiResponse<List<ProductCategory>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.findAll(), "获取分类成功"));
    }

    @PostMapping("/product/categories")
    public ResponseEntity<ApiResponse<ProductCategory>> createCategory(@RequestBody ProductCategory category) {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.save(category), "创建分类成功"));
    }

    @PutMapping("/product/categories/{id}")
    public ResponseEntity<ApiResponse<ProductCategory>> updateCategory(@PathVariable Long id, @RequestBody ProductCategory categoryDetails) {
        ProductCategory category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        return ResponseEntity.ok(ApiResponse.success(categoryRepository.save(category), "更新分类成功"));
    }

    @DeleteMapping("/product/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "删除分类成功"));
    }

    // ==========================================
    // 2. 核心商品管理 (Products)
    // 解决报错 2：/api/admin/products
    // ==========================================

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {

        // 取出所有商品
        List<Product> allProducts = productRepository.findAll();

        // 1. 在内存中进行安全过滤（防 Repository 缺方法报错）
        List<Product> filteredProducts = allProducts.stream()
                .filter(p -> {
                    boolean matchCategory = (categoryId == null || categoryId.equals(p.getCategoryId()));
                    boolean matchKeyword = (keyword == null || keyword.trim().isEmpty() ||
                            (p.getName() != null && p.getName().contains(keyword)));
                    return matchCategory && matchKeyword;
                })
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // 按时间倒序
                .collect(Collectors.toList());

        // 2. 手动进行分页切割
        int start = Math.min(page * size, filteredProducts.size());
        int end = Math.min(start + size, filteredProducts.size());
        List<Product> pagedList = filteredProducts.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> resultPage = new PageImpl<>(pagedList, pageable, filteredProducts.size());

        return ResponseEntity.ok(ApiResponse.success(resultPage, "获取商品列表成功"));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        if (product.getIsActive() == null) product.setIsActive(true); // 默认上架
        return ResponseEntity.ok(ApiResponse.success(productRepository.save(product), "创建商品成功"));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setCurrentPrice(productDetails.getCurrentPrice());
        product.setStock(productDetails.getStock());
        product.setCategoryId(productDetails.getCategoryId());
        product.setImages(productDetails.getImages());
        product.setSpecs(productDetails.getSpecs());
        return ResponseEntity.ok(ApiResponse.success(productRepository.save(product), "更新商品成功"));
    }

    // 快捷上下架功能
    @PutMapping("/products/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean isActive = body.get("isActive");
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        product.setIsActive(isActive);
        productRepository.save(product);
        return ResponseEntity.ok(ApiResponse.success(null, "更新状态成功"));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "删除商品成功"));
    }
}