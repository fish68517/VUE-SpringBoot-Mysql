package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.CartDTO;
import com.shenyang.musicfestival.dto.CartItemDTO;
import com.shenyang.musicfestival.dto.CreateProductOrderRequest;
import com.shenyang.musicfestival.dto.OrderDTO;
import com.shenyang.musicfestival.entity.Order;
import com.shenyang.musicfestival.entity.OrderItem;
import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.repository.OrderItemRepository;
import com.shenyang.musicfestival.repository.OrderRepository;
import com.shenyang.musicfestival.repository.ProductRepository;
import com.shenyang.musicfestival.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    private OrderService orderService;

    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_ID = 1L;
    private static final Long ORDER_ID = 1L;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, productRepository, cartService);
    }

    @Test
    void testCreateProductOrder_Success() {
        // Arrange
        CartItemDTO cartItem = CartItemDTO.builder()
            .id(1L)
            .productId(PRODUCT_ID)
            .productName("T-Shirt")
            .price(BigDecimal.valueOf(99.99))
            .quantity(2)
            .subtotal(BigDecimal.valueOf(199.98))
            .build();

        CartDTO cart = CartDTO.builder()
            .items(List.of(cartItem))
            .totalPrice(BigDecimal.valueOf(199.98))
            .totalQuantity(2)
            .build();

        CreateProductOrderRequest request = CreateProductOrderRequest.builder()
            .cartItemIds(List.of(1L))
            .shippingAddress("123 Main St")
            .build();

        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(10)
            .isActive(true)
            .build();

        Order savedOrder = Order.builder()
            .id(ORDER_ID)
            .userId(USER_ID)
            .orderType("product")
            .totalAmount(BigDecimal.valueOf(199.98))
            .status("paid")
            .shippingAddress("123 Main St")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(cartService.getCart(USER_ID)).thenReturn(cart);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.findByOrderId(ORDER_ID)).thenReturn(new ArrayList<>());

        // Act
        OrderDTO result = orderService.createProductOrder(USER_ID, request);

        // Assert
        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
        assertEquals(USER_ID, result.getUserId());
        assertEquals("product", result.getOrderType());
        assertEquals("paid", result.getStatus());
        assertEquals(BigDecimal.valueOf(199.98), result.getTotalAmount());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(productRepository, times(1)).save(any(Product.class));
        verify(cartService, times(1)).removeFromCart(USER_ID, 1L);
    }

    @Test
    void testCreateProductOrder_EmptyShippingAddress() {
        // Arrange
        CreateProductOrderRequest request = CreateProductOrderRequest.builder()
            .cartItemIds(List.of(1L))
            .shippingAddress("")
            .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createProductOrder(USER_ID, request)
        );
    }

    @Test
    void testCreateProductOrder_EmptyCart() {
        // Arrange
        CartDTO cart = CartDTO.builder()
            .items(new ArrayList<>())
            .totalPrice(BigDecimal.ZERO)
            .totalQuantity(0)
            .build();

        CreateProductOrderRequest request = CreateProductOrderRequest.builder()
            .cartItemIds(List.of(1L))
            .shippingAddress("123 Main St")
            .build();

        when(cartService.getCart(USER_ID)).thenReturn(cart);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createProductOrder(USER_ID, request)
        );
    }

    @Test
    void testCreateProductOrder_ProductNotFound() {
        // Arrange
        CartItemDTO cartItem = CartItemDTO.builder()
            .id(1L)
            .productId(PRODUCT_ID)
            .productName("T-Shirt")
            .price(BigDecimal.valueOf(99.99))
            .quantity(2)
            .subtotal(BigDecimal.valueOf(199.98))
            .build();

        CartDTO cart = CartDTO.builder()
            .items(List.of(cartItem))
            .totalPrice(BigDecimal.valueOf(199.98))
            .totalQuantity(2)
            .build();

        CreateProductOrderRequest request = CreateProductOrderRequest.builder()
            .cartItemIds(List.of(1L))
            .shippingAddress("123 Main St")
            .build();

        when(cartService.getCart(USER_ID)).thenReturn(cart);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createProductOrder(USER_ID, request)
        );
    }

    @Test
    void testCreateProductOrder_InsufficientStock() {
        // Arrange
        CartItemDTO cartItem = CartItemDTO.builder()
            .id(1L)
            .productId(PRODUCT_ID)
            .productName("T-Shirt")
            .price(BigDecimal.valueOf(99.99))
            .quantity(20)
            .subtotal(BigDecimal.valueOf(1999.80))
            .build();

        CartDTO cart = CartDTO.builder()
            .items(List.of(cartItem))
            .totalPrice(BigDecimal.valueOf(1999.80))
            .totalQuantity(20)
            .build();

        CreateProductOrderRequest request = CreateProductOrderRequest.builder()
            .cartItemIds(List.of(1L))
            .shippingAddress("123 Main St")
            .build();

        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(5)
            .isActive(true)
            .build();

        when(cartService.getCart(USER_ID)).thenReturn(cart);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createProductOrder(USER_ID, request)
        );
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        Order order = Order.builder()
            .id(ORDER_ID)
            .userId(USER_ID)
            .orderType("product")
            .totalAmount(BigDecimal.valueOf(199.98))
            .status("paid")
            .shippingAddress("123 Main St")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(ORDER_ID)).thenReturn(new ArrayList<>());

        // Act
        Optional<OrderDTO> result = orderService.getOrderById(ORDER_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ORDER_ID, result.get().getId());
        assertEquals(USER_ID, result.get().getUserId());
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // Act
        Optional<OrderDTO> result = orderService.getOrderById(ORDER_ID);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductOrdersByUserId_Success() {
        // Arrange
        Order order1 = Order.builder()
            .id(1L)
            .userId(USER_ID)
            .orderType("product")
            .totalAmount(BigDecimal.valueOf(199.98))
            .status("paid")
            .shippingAddress("123 Main St")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Order order2 = Order.builder()
            .id(2L)
            .userId(USER_ID)
            .orderType("product")
            .totalAmount(BigDecimal.valueOf(99.99))
            .status("shipped")
            .shippingAddress("456 Oak Ave")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(orderRepository.findByUserIdAndOrderType(USER_ID, "product"))
            .thenReturn(List.of(order1, order2));
        when(orderItemRepository.findByOrderId(any())).thenReturn(new ArrayList<>());

        // Act
        List<OrderDTO> result = orderService.getProductOrdersByUserId(USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByUserIdAndOrderType(USER_ID, "product");
    }

    @Test
    void testGetProductOrdersByUserIdAndStatus_Success() {
        // Arrange
        Order order = Order.builder()
            .id(ORDER_ID)
            .userId(USER_ID)
            .orderType("product")
            .totalAmount(BigDecimal.valueOf(199.98))
            .status("paid")
            .shippingAddress("123 Main St")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(orderRepository.findByUserIdAndOrderTypeAndStatus(USER_ID, "product", "paid"))
            .thenReturn(List.of(order));
        when(orderItemRepository.findByOrderId(ORDER_ID)).thenReturn(new ArrayList<>());

        // Act
        List<OrderDTO> result = orderService.getProductOrdersByUserIdAndStatus(USER_ID, "paid");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("paid", result.get(0).getStatus());
        verify(orderRepository, times(1)).findByUserIdAndOrderTypeAndStatus(USER_ID, "product", "paid");
    }

    @Test
    void testGetProductOrdersByUserIdAndStatus_NoResults() {
        // Arrange
        when(orderRepository.findByUserIdAndOrderTypeAndStatus(USER_ID, "product", "shipped"))
            .thenReturn(new ArrayList<>());

        // Act
        List<OrderDTO> result = orderService.getProductOrdersByUserIdAndStatus(USER_ID, "shipped");

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(orderRepository, times(1)).findByUserIdAndOrderTypeAndStatus(USER_ID, "product", "shipped");
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        Order order = Order.builder()
            .id(ORDER_ID)
            .userId(USER_ID)
            .orderType("product")
            .totalAmount(BigDecimal.valueOf(199.98))
            .status("paid")
            .shippingAddress("123 Main St")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Order updatedOrder = Order.builder()
            .id(ORDER_ID)
            .userId(USER_ID)
            .orderType("product")
            .totalAmount(BigDecimal.valueOf(199.98))
            .status("shipped")
            .shippingAddress("123 Main St")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(orderItemRepository.findByOrderId(ORDER_ID)).thenReturn(new ArrayList<>());

        // Act
        OrderDTO result = orderService.updateOrderStatus(ORDER_ID, "shipped");

        // Assert
        assertNotNull(result);
        assertEquals("shipped", result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

}
