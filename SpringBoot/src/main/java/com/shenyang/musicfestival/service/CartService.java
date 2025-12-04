package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.CartDTO;
import com.shenyang.musicfestival.dto.CartItemDTO;

/**
 * Service interface for shopping cart operations
 */
public interface CartService {

    /**
     * Add item to cart
     * @param userId user ID
     * @param productId product ID
     * @param quantity quantity to add
     * @return updated cart
     */
    CartDTO addToCart(Long userId, Long productId, Integer quantity);

    /**
     * Get user's cart
     * @param userId user ID
     * @return cart DTO
     */
    CartDTO getCart(Long userId);

    /**
     * Remove item from cart
     * @param userId user ID
     * @param cartItemId cart item ID
     * @return updated cart
     */
    CartDTO removeFromCart(Long userId, Long cartItemId);

    /**
     * Clear user's cart
     * @param userId user ID
     */
    void clearCart(Long userId);

}
