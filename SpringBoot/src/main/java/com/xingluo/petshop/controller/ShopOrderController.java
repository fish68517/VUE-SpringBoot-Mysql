package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
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
 * 店家订单管理控制器
 */
@RestController
@RequestMapping("/api/shop/order")
@RequiredArgsConstructor
public class ShopOrderController {

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

        // 填充订单项
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
     * 获取店铺订单列表
     * @param shopId 店铺ID
     * @return 订单列表
     */
    @GetMapping("/list")
    public ApiResponse<List<OrderVO>> getShopOrders(@RequestParam Long shopId) {
        List<Order> orders = orderService.getShopOrders(shopId);
        // 转换为 VO 列表
        List<OrderVO> voList = orders.stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());
        return ApiResponse.ok(voList);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderVO> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return ApiResponse.ok(convertToOrderVO(order));
    }

    /**
     * 发货
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/ship")
    public ApiResponse<OrderVO> shipOrder(@PathVariable Long id) {
        Order order = orderService.shipOrder(id);
        return ApiResponse.ok(convertToOrderVO(order));
    }
}