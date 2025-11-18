package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.Cart;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.repository.CartRepository;
import com.xingluo.petshop.repository.ProductRepository;
import com.xingluo.petshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 购物车服务实现类
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        // 验证商品是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证商品状态（1表示上架）
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品未上架");
        }
        
        // 验证库存是否充足
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }
        
        // 检查购物车中是否已存在该商品
        Optional<Cart> existingCart = cartRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingCart.isPresent()) {
            // 如果已存在，更新数量
            Cart cart = existingCart.get();
            int newQuantity = cart.getQuantity() + quantity;
            
            // 再次验证库存
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("商品库存不足");
            }
            
            cart.setQuantity(newQuantity);
            return cartRepository.save(cart);
        } else {
            // 如果不存在，创建新的购物车项
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            return cartRepository.save(cart);
        }
    }
    
    @Override
    public List<Cart> getUserCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public Cart updateCartQuantity(Long cartId, Integer quantity) {
        // 查找购物车项
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));
        
        // 验证商品库存
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }
        
        // 更新数量
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }
    
    @Override
    @Transactional
    public void deleteCartItem(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("购物车项不存在");
        }
        cartRepository.deleteById(cartId);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }
}
