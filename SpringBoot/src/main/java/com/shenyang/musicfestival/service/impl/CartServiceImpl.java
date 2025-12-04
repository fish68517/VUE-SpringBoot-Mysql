package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.dto.CartDTO;
import com.shenyang.musicfestival.dto.CartItemDTO;
import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.repository.ProductRepository;
import com.shenyang.musicfestival.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.*;

/**
 * Implementation of CartService using Session storage
 */
@Service
@SessionScope
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    
    // Session-based cart storage: userId -> List of CartItems
    private final Map<Long, List<CartItemDTO>> cartStorage = new HashMap<>();
    private Long currentUserId;

    @Override
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("商品不存在"));

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("商品已下架");
        }

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("商品库存不足");
        }

        List<CartItemDTO> cart = cartStorage.computeIfAbsent(userId, k -> new ArrayList<>());

        // Check if product already exists in cart
        Optional<CartItemDTO> existingItem = cart.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItemDTO item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("商品库存不足");
            }
            item.setQuantity(newQuantity);
            item.setSubtotal(product.getCurrentPrice().multiply(BigDecimal.valueOf(newQuantity)));
        } else {
            CartItemDTO newItem = CartItemDTO.builder()
                .id(System.currentTimeMillis()) // Generate unique ID for cart item
                .productId(productId)
                .productName(product.getName())
                .price(product.getCurrentPrice())
                .quantity(quantity)
                .subtotal(product.getCurrentPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
            cart.add(newItem);
        }

        return buildCartDTO(userId);
    }

    @Override
    public CartDTO getCart(Long userId) {
        return buildCartDTO(userId);
    }

    @Override
    public CartDTO removeFromCart(Long userId, Long cartItemId) {
        List<CartItemDTO> cart = cartStorage.get(userId);
        if (cart != null) {
            cart.removeIf(item -> item.getId().equals(cartItemId));
        }
        return buildCartDTO(userId);
    }

    @Override
    public void clearCart(Long userId) {
        cartStorage.remove(userId);
    }

    /**
     * Build CartDTO from session storage
     */
    private CartDTO buildCartDTO(Long userId) {
        List<CartItemDTO> items = cartStorage.getOrDefault(userId, new ArrayList<>());
        
        BigDecimal totalPrice = items.stream()
            .map(CartItemDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalQuantity = items.stream()
            .mapToInt(CartItemDTO::getQuantity)
            .sum();

        return CartDTO.builder()
            .items(new ArrayList<>(items))
            .totalPrice(totalPrice)
            .totalQuantity(totalQuantity)
            .build();
    }

}
