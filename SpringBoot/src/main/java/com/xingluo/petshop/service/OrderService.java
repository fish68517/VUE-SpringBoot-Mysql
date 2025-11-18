package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单（包含库存扣减和购物车清空）
     * @param userId 用户ID
     * @param cartIds 购物车ID列表
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @param receiverAddress 收货地址
     * @param remark 备注
     * @return 创建的订单
     */
    Order createOrder(Long userId, List<Long> cartIds, String receiverName, 
                     String receiverPhone, String receiverAddress, String remark);
    
    /**
     * 查询用户订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getUserOrders(Long userId);
    
    /**
     * 查询订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    Order getOrderDetail(Long orderId);
    
    /**
     * 支付订单（更新状态）
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    Order payOrder(Long orderId);
    
    /**
     * 取消订单（恢复库存）
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    Order cancelOrder(Long orderId);
    
    /**
     * 确认收货
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    Order completeOrder(Long orderId);
    
    /**
     * 查询店铺订单列表
     * @param shopId 店铺ID
     * @return 订单列表
     */
    List<Order> getShopOrders(Long shopId);
    
    /**
     * 发货（更新订单状态和发货时间）
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    Order shipOrder(Long orderId);
}
