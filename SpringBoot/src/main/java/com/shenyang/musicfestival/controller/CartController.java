package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.AddToCartRequest;
import com.shenyang.musicfestival.dto.CartDTO;
import com.shenyang.musicfestival.service.CartService;
import com.shenyang.musicfestival.util.ApiResponse;
import com.shenyang.musicfestival.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Shopping Cart operations
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    /**
     * Add item to cart
     * POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @RequestHeader("Authorization") String token,
            @RequestBody AddToCartRequest request) {
        
        try {
            Long userId = jwtUtil.extractUserId(token);
            
            if (request.getProductId() == null || request.getQuantity() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("商品ID和数量不能为空"));
            }

            CartDTO cart = cartService.addToCart(userId, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(ApiResponse.success(cart, "添加到购物车成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("添加到购物车失败"));
        }
    }

    /**
     * Get user's cart
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = jwtUtil.extractUserId(token);
            CartDTO cart = cartService.getCart(userId);
            return ResponseEntity.ok(ApiResponse.success(cart, "获取购物车成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取购物车失败"));
        }
    }

    /**
     * Remove item from cart
     * DELETE /api/cart/:id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CartDTO>> removeFromCart(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        
        try {
            Long userId = jwtUtil.extractUserId(token);
            CartDTO cart = cartService.removeFromCart(userId, id);
            return ResponseEntity.ok(ApiResponse.success(cart, "从购物车删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("删除购物车项失败"));
        }
    }

}
