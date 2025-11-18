package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 店铺 Repository
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    /**
     * 根据用户ID查询店铺
     */
    Optional<Shop> findByUserId(Long userId);
    
    /**
     * 检查用户是否已有店铺
     */
    boolean existsByUserId(Long userId);
}
