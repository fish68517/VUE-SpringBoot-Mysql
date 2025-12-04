package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.dto.CartDTO;
import com.shenyang.musicfestival.dto.CartItemDTO;
import com.shenyang.musicfestival.dto.CreateProductOrderRequest;
import com.shenyang.musicfestival.dto.OrderDTO;
import com.shenyang.musicfestival.dto.OrderItemDTO;
import com.shenyang.musicfestival.entity.Order;
import com.shenyang.musicfestival.entity.OrderItem;
import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.repository.OrderItemRepository;
import com.shenyang.musicfestival.repository.OrderRepository;
import com.shenyang.musicfestival.repository.ProductRepository;
import com.shenyang.musicfestival.service.CartService;
import com.shenyang.musicfestival.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of OrderService
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderDTO createProductOrder(Long userId, CreateProductOrderRequest request) {
        if (request.getShippingAddress() == null || request.getShippingAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("收货地址不能为空");
        }

        // Get user's cart
        CartDTO cart = cartService.getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("购物车为空");
        }

        // Filter cart items to order
        List<CartItemDTO> itemsToOrder = cart.getItems().stream()
            .filter(item -> request.getCartItemIds().contains(item.getId()))
            .collect(Collectors.toList());

        if (itemsToOrder.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }

        // Calculate total amount and validate stock
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemDTO cartItem : itemsToOrder) {
            Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + cartItem.getProductId()));

            if (!product.getIsActive()) {
                throw new IllegalArgumentException("商品已下架: " + product.getName());
            }

            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("商品库存不足: " + product.getName());
            }

            totalAmount = totalAmount.add(cartItem.getSubtotal());
        }

        // Create order
        Order order = Order.builder()
            .userId(userId)
            .orderType("product")
            .totalAmount(totalAmount)
            .status("paid")
            .shippingAddress(request.getShippingAddress())
            .build();

        Order savedOrder = orderRepository.save(order);

        // Create order items and reduce product stock
        for (CartItemDTO cartItem : itemsToOrder) {
            Product product = productRepository.findById(cartItem.getProductId()).get();

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                .orderId(savedOrder.getId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getPrice())
                .build();

            orderItemRepository.save(orderItem);

            // Reduce product stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Remove from cart
            cartService.removeFromCart(userId, cartItem.getId());
        }

        return convertToDTO(savedOrder);
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(this::convertToDTO);
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getProductOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdAndOrderType(userId, "product").stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getProductOrdersByUserIdAndStatus(Long userId, String status) {
        return orderRepository.findByUserIdAndOrderTypeAndStatus(userId, "product", status).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO updateTrackingNumber(Long orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        order.setTrackingNumber(trackingNumber);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    /**
     * Convert Order entity to OrderDTO with order items
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDTO> itemDTOs = orderItems.stream()
            .map(item -> {
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                return OrderItemDTO.builder()
                    .id(item.getId())
                    .orderId(item.getOrderId())
                    .productId(item.getProductId())
                    .productName(product != null ? product.getName() : "")
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            })
            .collect(Collectors.toList());

        return OrderDTO.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .orderType(order.getOrderType())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .shippingAddress(order.getShippingAddress())
            .trackingNumber(order.getTrackingNumber())
            .items(itemDTOs)
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }

}
