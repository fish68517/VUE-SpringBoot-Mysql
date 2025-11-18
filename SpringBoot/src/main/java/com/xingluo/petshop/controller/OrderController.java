package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CreateOrderDTO;
import com.xingluo.petshop.entity.Order;
import com.xingluo.petshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * 创建订单
     * @param userId 用户ID（实际应从登录会话获取）
     * @param dto 创建订单DTO
     * @return 创建的订单
     */
    @PostMapping
    public ApiResponse<Order> createOrder(@RequestParam Long userId, @RequestBody CreateOrderDTO dto) {
        Order order = orderService.createOrder(
                userId,
                dto.getCartIds(),
                dto.getReceiverName(),
                dto.getReceiverPhone(),
                dto.getReceiverAddress(),
                dto.getRemark()
        );
        return ApiResponse.success(order);
    }
    
    /**
     * 获取订单列表
     * @param userId 用户ID（实际应从登录会话获取）
     * @return 订单列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Order>> getOrderList(@RequestParam Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ApiResponse.success(orders);
    }
    
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return ApiResponse.success(order);
    }
    
    /**
     * 支付订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/pay")
    public ApiResponse<Order> payOrder(@PathVariable Long id) {
        Order order = orderService.payOrder(id);
        return ApiResponse.success(order);
    }
    
    /**
     * 取消订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/cancel")
    public ApiResponse<Order> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ApiResponse.success(order);
    }
    
    /**
     * 确认收货
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/complete")
    public ApiResponse<Order> completeOrder(@PathVariable Long id) {
        Order order = orderService.completeOrder(id);
        return ApiResponse.success(order);
    }
}
