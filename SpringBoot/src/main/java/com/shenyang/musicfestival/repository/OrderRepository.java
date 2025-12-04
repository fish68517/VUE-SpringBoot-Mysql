package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders by user ID
     */
    List<Order> findByUserId(Long userId);

    /**
     * Find all orders by user ID and order type
     */
    List<Order> findByUserIdAndOrderType(Long userId, String orderType);

    /**
     * Find all orders by user ID, order type, and status
     */
    List<Order> findByUserIdAndOrderTypeAndStatus(Long userId, String orderType, String status);

}
