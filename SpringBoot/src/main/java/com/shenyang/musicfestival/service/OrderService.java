package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.CreateProductOrderRequest;
import com.shenyang.musicfestival.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Order operations
 */
public interface OrderService {

    /**
     * Create a product order from cart items
     */
    OrderDTO createProductOrder(Long userId, CreateProductOrderRequest request);

    /**
     * Get order by ID
     */
    Optional<OrderDTO> getOrderById(Long orderId);

    /**
     * Get all orders by user ID
     */
    List<OrderDTO> getOrdersByUserId(Long userId);

    /**
     * Get all product orders by user ID
     */
    List<OrderDTO> getProductOrdersByUserId(Long userId);

    /**
     * Get product orders by user ID and status
     */
    List<OrderDTO> getProductOrdersByUserIdAndStatus(Long userId, String status);

    /**
     * Update order status
     */
    OrderDTO updateOrderStatus(Long orderId, String status);

    /**
     * Update tracking number
     */
    OrderDTO updateTrackingNumber(Long orderId, String trackingNumber);

}
