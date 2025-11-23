package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CreateOrderDTO;
import com.xingluo.petshop.dto.OrderItemVO;
import com.xingluo.petshop.dto.OrderVO;
import com.xingluo.petshop.entity.Order;
import com.xingluo.petshop.entity.OrderItem;
import com.xingluo.petshop.repository.OrderItemRepository;
import com.xingluo.petshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    /**
     * 实体转 OrderVO
     */
    private OrderVO convertToOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 处理关联的店铺名称 (如果存在)
        if (order.getShop() != null) {
            try {
                vo.setShopName(order.getShop().getName());
            } catch (Exception e) {
                // 忽略懒加载异常
            }
        }

        // 填充订单项 (可选，视业务需求而定，这里可以先填上方便前端)
        try {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            List<OrderItemVO> itemVOs = items.stream()
                    .map(this::convertToItemVO)
                    .collect(Collectors.toList());
            vo.setOrderItems(itemVOs);
        } catch (Exception e) {
            // 忽略查询异常
        }

        return vo;
    }

    /**
     * 实体转 OrderItemVO
     */
    private OrderItemVO convertToItemVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        BeanUtils.copyProperties(item, vo);
        return vo;
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderVO> createOrder(@RequestParam Long userId, @RequestBody CreateOrderDTO dto) {
        Order order = orderService.createOrder(
                userId,
                dto.getCartIds(),
                dto.getReceiverName(),
                dto.getReceiverPhone(),
                dto.getReceiverAddress(),
                dto.getRemark()
        );
        return ApiResponse.ok(convertToOrderVO(order));
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public ApiResponse<List<OrderVO>> getOrderList(@RequestParam Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderVO> voList = orders.stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());
        return ApiResponse.ok(voList);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderVO> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return ApiResponse.ok(convertToOrderVO(order));
    }

    /**
     * 支付订单
     */
    @PutMapping("/{id}/pay")
    public ApiResponse<OrderVO> payOrder(@PathVariable Long id) {
        Order order = orderService.payOrder(id);
        return ApiResponse.ok(convertToOrderVO(order));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderVO> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ApiResponse.ok(convertToOrderVO(order));
    }

    /**
     * 确认收货
     */
    @PutMapping("/{id}/complete")
    public ApiResponse<OrderVO> completeOrder(@PathVariable Long id) {
        Order order = orderService.completeOrder(id);
        return ApiResponse.ok(convertToOrderVO(order));
    }

    /**
     * 获取订单商品明细
     * 这里的返回值也要改成 VO List
     */
    @GetMapping("/{orderId}/items")
    public ApiResponse<List<OrderItemVO>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        List<OrderItemVO> voList = items.stream()
                .map(this::convertToItemVO)
                .collect(Collectors.toList());
        return ApiResponse.ok(voList);
    }
}