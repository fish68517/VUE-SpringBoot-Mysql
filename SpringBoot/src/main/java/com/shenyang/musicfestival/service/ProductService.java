package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Product operations
 */
public interface ProductService {

    /**
     * Get all product categories
     */
    List<ProductCategory> getAllCategories();

    /**
     * Get product category by ID
     */
    Optional<ProductCategory> getCategoryById(Long id);

    /**
     * Get all active products with pagination
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * Get products by category with pagination
     */
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);

    /**
     * Get product by ID
     */
    Optional<Product> getProductById(Long id);

}
