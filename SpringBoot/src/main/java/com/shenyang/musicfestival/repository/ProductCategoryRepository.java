package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ProductCategory entity
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

}
