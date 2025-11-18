package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 添加商品到购物车（包含库存验证）
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 购物车项
     */
    Cart addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 查询用户购物车
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> getUserCart(Long userId);
    
    /**
     * 更新购物车商品数量
     * @param cartId 购物车ID
     * @param quantity 新数量
     * @return 更新后的购物车项
     */
    Cart updateCartQuantity(Long cartId, Integer quantity);
    
    /**
     * 删除购物车商品
     * @param cartId 购物车ID
     */
    void deleteCartItem(Long cartId);
    
    /**
     * 清空购物车
     * @param userId 用户ID
     */
    void clearCart(Long userId);
}
