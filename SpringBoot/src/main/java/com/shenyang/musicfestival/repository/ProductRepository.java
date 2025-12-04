package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find products by category ID with pagination
     */
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    /**
     * Find all active products with pagination
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);

}
