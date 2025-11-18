package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车Repository接口
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * 查询用户购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> findByUserId(Long userId);
    
    /**
     * 查询用户购物车中的指定商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 删除用户购物车中的所有商品
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
}
