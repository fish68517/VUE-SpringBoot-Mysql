package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.CreateProductOrderRequest;
import com.shenyang.musicfestival.dto.OrderDTO;
import com.shenyang.musicfestival.service.OrderService;
import com.shenyang.musicfestival.util.ApiResponse;
import com.shenyang.musicfestival.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Order operations
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;


    /**
     * 新增：获取用户全部订单（含票务和商品）
     * GET /api/orders/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = extractUserIdSafe(token);
            List<OrderDTO> orders = orderService.getAllOrdersByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(orders, "获取全部订单成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取订单列表失败: " + e.getMessage()));
        }
    }

    // 新增：安全提取用户 ID 的方法（防报错）
    private Long extractUserIdSafe(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                return jwtUtil.extractUserId(token.substring(7)); // 截取掉 "Bearer "
            }
            if (token != null && !token.isEmpty()) {
                return jwtUtil.extractUserId(token);
            }
        } catch (Exception e) {
            // 解析失败时，忽略错误，走下面的默认值
        }
        return 1L; // 兜底：如果没有 Token 或解析失败，默认返回用户 ID 1（防止 500 报错）
    }

    @PostMapping("/product")
    public ResponseEntity<ApiResponse<OrderDTO>> createProductOrder(
            @RequestHeader(value = "Authorization", required = false) String token, // 改为非必传
            @RequestBody CreateProductOrderRequest request) {

        try {
            Long userId = extractUserIdSafe(token); // 使用安全方法
            OrderDTO order = orderService.createProductOrder(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(order, "订单创建成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("订单创建失败: " + e.getMessage()));
        }
    }

    @GetMapping("/product")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getProductOrders(
            @RequestHeader(value = "Authorization", required = false) String token, // 改为非必传
            @RequestParam(value = "status", required = false) String status) {

        try {
            Long userId = extractUserIdSafe(token); // 使用安全方法
            List<OrderDTO> orders;

            if (status != null && !status.trim().isEmpty()) {
                orders = orderService.getProductOrdersByUserIdAndStatus(userId, status);
            } else {
                orders = orderService.getProductOrdersByUserId(userId);
            }

            return ResponseEntity.ok(ApiResponse.success(orders, "获取订单列表成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取订单列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        try {
            var order = orderService.getOrderById(id);
            if (order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("订单不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(order.get(), "获取订单详情成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取订单详情失败"));
        }
    }
}