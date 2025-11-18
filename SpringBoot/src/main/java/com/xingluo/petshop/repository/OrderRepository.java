package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单Repository接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 查询用户订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单
     */
    Optional<Order> findByOrderNo(String orderNo);
    
    /**
     * 查询店铺订单列表
     * @param shopId 店铺ID
     * @return 订单列表
     */
    List<Order> findByShopIdOrderByCreateTimeDesc(Long shopId);
}
