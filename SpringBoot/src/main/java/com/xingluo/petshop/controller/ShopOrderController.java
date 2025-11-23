package com.xingluo.petshop.controller;


import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.entity.Order;
import com.xingluo.petshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店家订单管理控制器
 */
@RestController
@RequestMapping("/api/shop/order")
@RequiredArgsConstructor
public class ShopOrderController {
    
    private final OrderService orderService;
    
    /**
     * 获取店铺订单列表
     * @param shopId 店铺ID
     * @return 订单列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Order>> getShopOrders(@RequestParam Long shopId) {
        List<Order> orders = orderService.getShopOrders(shopId);
        return ApiResponse.ok(orders);
    }
    
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return ApiResponse.ok(order);
    }
    
    /**
     * 发货
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/ship")
    public ApiResponse<Order> shipOrder(@PathVariable Long id) {
        Order order = orderService.shipOrder(id);
        return ApiResponse.ok(order);
    }
}
