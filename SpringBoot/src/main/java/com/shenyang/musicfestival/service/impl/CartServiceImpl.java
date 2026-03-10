package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.dto.CartDTO;
import com.shenyang.musicfestival.dto.CartItemDTO;
import com.shenyang.musicfestival.entity.CartItem;
import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.repository.CartItemRepository;
import com.shenyang.musicfestival.repository.ProductRepository;
import com.shenyang.musicfestival.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类（已升级为数据库持久化存储）
 */
@Service
// 注意：删除了之前的 @SessionScope
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    // 新增：实现修改数量逻辑
    @Override
    @Transactional
    public CartDTO updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        // 1. 如果数量被改成 0 或负数，直接走删除逻辑
        if (quantity <= 0) {
            return removeFromCart(userId, cartItemId);
        }

        // 2. 查找数据库中的这条购物车记录
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("购物车项不存在"));

        // 3. 安全校验：只能修改自己的购物车
        if (!cartItem.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权修改此购物车项");
        }

        // 4. 查找商品并校验库存
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("商品库存不足，当前剩余：" + product.getStock());
        }

        // 5. 更新数量并保存
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        // 6. 返回最新计算好的购物车数据
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("商品已下架");
        }

        // 查找购物车中是否已有该商品
        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingItemOpt.isPresent()) {
            // 如果已有，增加数量
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("商品库存不足");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // 如果没有，新增一条记录
            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("商品库存不足");
            }
            CartItem newItem = CartItem.builder()
                    .userId(userId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            cartItemRepository.save(newItem);
        }

        return getCart(userId);
    }

    @Override
    public CartDTO getCart(Long userId) {
        // 从数据库获取该用户所有的购物车项
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        // 转换为前端需要的 DTO
        List<CartItemDTO> itemDTOs = cartItems.stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null) return null; // 忽略已删除的商品

            return CartItemDTO.builder()
                    .id(item.getId()) // 这里的 id 现在是数据库 cart_items 表的主键
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getCurrentPrice())
                    .quantity(item.getQuantity())
                    .subtotal(product.getCurrentPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
        }).filter(item -> item != null).collect(Collectors.toList());

        // 计算总价和总数量
        BigDecimal totalPrice = itemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalQuantity = itemDTOs.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        return CartDTO.builder()
                .items(itemDTOs)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .build();
    }

    @Override
    @Transactional
    public CartDTO removeFromCart(Long userId, Long cartItemId) {
        cartItemRepository.deleteByUserIdAndId(userId, cartItemId);
        return getCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}