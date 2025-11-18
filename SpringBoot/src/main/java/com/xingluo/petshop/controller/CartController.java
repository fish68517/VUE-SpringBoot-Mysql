package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.AddToCartDTO;
import com.xingluo.petshop.dto.CartVO;
import com.xingluo.petshop.dto.UpdateCartDTO;
import com.xingluo.petshop.entity.Cart;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.repository.ProductRepository;
import com.xingluo.petshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车控制器
 * 提供购物车的添加、查询、更新和删除等RESTful API
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    private final ProductRepository productRepository;
    
    /**
     * 添加商品到购物车
     * POST /api/cart
     */
    @PostMapping
    public ApiResponse<CartVO> addToCart(@RequestBody AddToCartDTO addToCartDTO) {
        Cart cart = cartService.addToCart(
                addToCartDTO.getUserId(),
                addToCartDTO.getProductId(),
                addToCartDTO.getQuantity()
        );
        
        // 转换为VO返回
        CartVO cartVO = convertToVO(cart);
        return ApiResponse.ok(cartVO);
    }
    
    /**
     * 获取购物车列表
     * GET /api/cart/list
     * 注意：实际项目中应该从token中获取userId，这里简化处理，通过请求参数传递
     */
    @GetMapping("/list")
    public ApiResponse<List<CartVO>> getCartList(@RequestParam Long userId) {
        List<Cart> carts = cartService.getUserCart(userId);
        
        // 转换为VO列表返回
        List<CartVO> cartVOList = carts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return ApiResponse.ok(cartVOList);
    }
    
    /**
     * 更新购物车商品数量
     * PUT /api/cart/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<CartVO> updateCartQuantity(@PathVariable Long id,
                                                   @RequestBody UpdateCartDTO updateCartDTO) {
        Cart cart = cartService.updateCartQuantity(id, updateCartDTO.getQuantity());
        
        // 转换为VO返回
        CartVO cartVO = convertToVO(cart);
        return ApiResponse.ok(cartVO);
    }
    
    /**
     * 删除购物车商品
     * DELETE /api/cart/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return ApiResponse.ok(null);
    }
    
    /**
     * 清空购物车
     * DELETE /api/cart/clear
     * 注意：实际项目中应该从token中获取userId，这里简化处理，通过请求参数传递
     */
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ApiResponse.ok(null);
    }
    
    /**
     * 将Cart实体转换为CartVO
     */
    private CartVO convertToVO(Cart cart) {
        CartVO cartVO = new CartVO();
        cartVO.setId(cart.getId());
        cartVO.setUserId(cart.getUserId());
        cartVO.setProductId(cart.getProductId());
        cartVO.setQuantity(cart.getQuantity());
        cartVO.setCreateTime(cart.getCreateTime());
        cartVO.setUpdateTime(cart.getUpdateTime());
        
        // 获取商品信息
        Product product = productRepository.findById(cart.getProductId()).orElse(null);
        if (product != null) {
            cartVO.setProductName(product.getName());
            cartVO.setProductImage(product.getImage());
            cartVO.setProductPrice(product.getPrice());
            cartVO.setProductStock(product.getStock());
            cartVO.setProductStatus(product.getStatus());
            
            // 计算小计
            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            cartVO.setSubtotal(subtotal);
        }
        
        return cartVO;
    }
}
