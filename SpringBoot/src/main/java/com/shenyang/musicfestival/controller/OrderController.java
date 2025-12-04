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
     * Create a product order
     * POST /api/orders/product
     */
    @PostMapping("/product")
    public ResponseEntity<ApiResponse<OrderDTO>> createProductOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateProductOrderRequest request) {
        
        try {
            Long userId = jwtUtil.extractUserId(token);
            OrderDTO order = orderService.createProductOrder(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "订单创建成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("订单创建失败"));
        }
    }

    /**
     * Get user's product orders
     * GET /api/orders/product?status=paid
     */
    @GetMapping("/product")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getProductOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "status", required = false) String status) {
        
        try {
            Long userId = jwtUtil.extractUserId(token);
            List<OrderDTO> orders;
            
            if (status != null && !status.trim().isEmpty()) {
                orders = orderService.getProductOrdersByUserIdAndStatus(userId, status);
            } else {
                orders = orderService.getProductOrdersByUserId(userId);
            }
            
            return ResponseEntity.ok(ApiResponse.success(orders, "获取订单列表成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取订单列表失败"));
        }
    }

    /**
     * Get order by ID
     * GET /api/orders/:id
     */
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取订单详情失败"));
        }
    }

}
