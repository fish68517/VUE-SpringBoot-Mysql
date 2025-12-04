package com.shenyang.musicfestival.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shenyang.musicfestival.dto.ProductCategoryDTO;
import com.shenyang.musicfestival.dto.ProductDTO;
import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.entity.ProductCategory;
import com.shenyang.musicfestival.service.ProductService;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Product operations
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    /**
     * Get all product categories
     * GET /api/products/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ProductCategoryDTO>>> getAllCategories() {
        List<ProductCategory> categories = productService.getAllCategories();
        List<ProductCategoryDTO> categoryDTOs = categories.stream()
            .map(this::convertCategoryToDTO)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(categoryDTOs, "获取商品分类列表成功"));
    }

    /**
     * Get all products with pagination and optional category filter
     * GET /api/products?page=0&size=10&categoryId=1
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;
        
        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId, pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }
        
        Page<ProductDTO> productDTOs = products.map(this::convertProductToDTO);
        return ResponseEntity.ok(ApiResponse.success(productDTOs, "获取商品列表成功"));
    }

    /**
     * Get product by ID
     * GET /api/products/:id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        var product = productService.getProductById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("商品不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(convertProductToDTO(product.get()), "获取商品详情成功"));
    }

    /**
     * Convert ProductCategory entity to ProductCategoryDTO
     */
    private ProductCategoryDTO convertCategoryToDTO(ProductCategory category) {
        return ProductCategoryDTO.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .build();
    }

    /**
     * Convert Product entity to ProductDTO
     */
    private ProductDTO convertProductToDTO(Product product) {
        List<String> images = null;
        Object specs = null;
        
        try {
            if (product.getImages() != null) {
                images = objectMapper.readValue(product.getImages(), List.class);
            }
            if (product.getSpecs() != null) {
                specs = objectMapper.readValue(product.getSpecs(), Object.class);
            }
        } catch (Exception e) {
            // If JSON parsing fails, leave as null
        }
        
        return ProductDTO.builder()
            .id(product.getId())
            .categoryId(product.getCategoryId())
            .name(product.getName())
            .description(product.getDescription())
            .images(images)
            .originalPrice(product.getOriginalPrice())
            .currentPrice(product.getCurrentPrice())
            .stock(product.getStock())
            .specs(specs)
            .isActive(product.getIsActive())
            .build();
    }

}
