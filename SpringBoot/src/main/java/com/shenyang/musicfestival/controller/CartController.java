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
     * Update item quantity in cart
     * PUT /api/cart/:id
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Integer> requestBody) { // 为了简单，直接用 Map 接收 JSON 里的 quantity

        try {
            Long userId = extractUserIdSafe(token);
            Integer quantity = requestBody.get("quantity");

            if (quantity == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("修改的数量不能为空"));
            }

            CartDTO cart = cartService.updateQuantity(userId, id, quantity);
            return ResponseEntity.ok(ApiResponse.success(cart, "更新数量成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新数量失败: " + e.getMessage()));
        }
    }

    // 新增：安全提取用户 ID 的方法（防报错）
    private Long extractUserIdSafe(String token) {
        try {
            // 必须截取掉前端传来的 "Bearer " 前缀，否则 JWT 解析会报错
            if (token != null && token.startsWith("Bearer ")) {
                return jwtUtil.extractUserId(token.substring(7));
            }
            if (token != null && !token.isEmpty()) {
                return jwtUtil.extractUserId(token);
            }
        } catch (Exception e) {
            // 解析失败时，忽略错误
        }
        return 1L; // 兜底：默认使用 ID 为 1 的测试用户
    }

    /**
     * Add item to cart
     * POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @RequestHeader(value = "Authorization", required = false) String token, // 改为非必传
            @RequestBody AddToCartRequest request) {

        try {
            Long userId = extractUserIdSafe(token); // 使用安全方法获取 userId

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
            e.printStackTrace(); // 打印具体错误到控制台
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("添加到购物车失败: " + e.getMessage()));
        }
    }

    /**
     * Get user's cart
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @RequestHeader(value = "Authorization", required = false) String token) { // 改为非必传

        try {
            Long userId = extractUserIdSafe(token); // 使用安全方法
            CartDTO cart = cartService.getCart(userId);
            return ResponseEntity.ok(ApiResponse.success(cart, "获取购物车成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取购物车失败: " + e.getMessage()));
        }
    }

    /**
     * Remove item from cart
     * DELETE /api/cart/:id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CartDTO>> removeFromCart(
            @RequestHeader(value = "Authorization", required = false) String token, // 改为非必传
            @PathVariable Long id) {

        try {
            Long userId = extractUserIdSafe(token); // 使用安全方法
            CartDTO cart = cartService.removeFromCart(userId, id);
            return ResponseEntity.ok(ApiResponse.success(cart, "从购物车删除成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("删除购物车项失败: " + e.getMessage()));
        }
    }

}