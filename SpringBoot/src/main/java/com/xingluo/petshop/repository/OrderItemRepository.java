package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单明细Repository接口
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 查询订单的所有明细
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    List<OrderItem> findByOrderId(Long orderId);
}
