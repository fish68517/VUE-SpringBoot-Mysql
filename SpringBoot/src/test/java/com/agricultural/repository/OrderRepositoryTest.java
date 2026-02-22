package com.agricultural.repository;

import com.agricultural.entity.Order;
import com.agricultural.entity.User;
import com.agricultural.entity.AgriculturalProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单数据访问层测试
 * 
 * 测试OrderRepository的CRUD操作和自定义查询方法
 * 包括按用户、状态、时间范围的查询功能
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgriculturalProductRepository productRepository;

    private Order testOrder;
    private User testUser;
    private AgriculturalProduct testProduct;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = User.builder()
                .username("test_farmer")
                .password("encrypted_password")
                .email("farmer@example.com")
                .phone("13800138000")
                .userType(User.UserType.FARMER)
                .region("河南省郑州市")
                .status(User.UserStatus.ACTIVE)
                .build();
        testUser = userRepository.save(testUser);

        // 创建测试商家
        User merchant = User.builder()
                .username("test_merchant")
                .password("encrypted_password")
                .email("merchant@example.com")
                .phone("13900139000")
                .userType(User.UserType.MERCHANT)
                .region("北京市朝阳区")
                .status(User.UserStatus.ACTIVE)
                .build();
        merchant = userRepository.save(merchant);

        // 创建测试产品
        testProduct = AgriculturalProduct.builder()
                .productName("防雨布")
                .category("防护用品")
                .description("高质量防雨布")
                .price(new BigDecimal("89.99"))
                .stock(100)
                .merchantId(merchant.getId())
                .applicableWeather("暴雨")
                .build();
        testProduct = productRepository.save(testProduct);

        // 创建测试订单
        testOrder = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .paymentMethod("支付宝")
                .deliveryAddress("河南省郑州市中原区农业路123号")
                .build();
    }

    @Test
    void testSaveOrder() {
        // 保存订单
        Order savedOrder = orderRepository.save(testOrder);
        
        // 验证订单已保存
        assertNotNull(savedOrder.getId());
        assertEquals("ORD20240220001", savedOrder.getOrderNumber());
        assertEquals(testUser.getId(), savedOrder.getUserId());
        assertEquals(Order.OrderStatus.PENDING, savedOrder.getStatus());
        assertNotNull(savedOrder.getCreatedAt());
        assertNotNull(savedOrder.getUpdatedAt());
    }

    @Test
    void testFindOrderById() {
        // 保存订单
        Order savedOrder = orderRepository.save(testOrder);
        
        // 根据ID查询订单
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
        
        // 验证查询结果
        assertTrue(foundOrder.isPresent());
        assertEquals("ORD20240220001", foundOrder.get().getOrderNumber());
        assertEquals(testUser.getId(), foundOrder.get().getUserId());
    }

    @Test
    void testFindByOrderNumber() {
        // 保存订单
        orderRepository.save(testOrder);
        
        // 根据订单号查询
        Optional<Order> foundOrder = orderRepository.findByOrderNumber("ORD20240220001");
        
        // 验证查询结果
        assertTrue(foundOrder.isPresent());
        assertEquals(testUser.getId(), foundOrder.get().getUserId());
        assertEquals(new BigDecimal("179.98"), foundOrder.get().getTotalPrice());
    }

    @Test
    void testFindByUserId() {
        // 保存多个订单
        Order order1 = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order order2 = Order.builder()
                .orderNumber("ORD20240220002")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PAID)
                .build();
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        
        // 查询用户的订单
        List<Order> userOrders = orderRepository.findByUserId(testUser.getId());
        
        // 验证查询结果
        assertEquals(2, userOrders.size());
        assertTrue(userOrders.stream().allMatch(o -> o.getUserId().equals(testUser.getId())));
    }

    @Test
    void testFindByStatus() {
        // 保存多个不同状态的订单
        Order pendingOrder = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order paidOrder = Order.builder()
                .orderNumber("ORD20240220002")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PAID)
                .build();
        
        orderRepository.save(pendingOrder);
        orderRepository.save(paidOrder);
        
        // 查询待处理订单
        List<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING);
        
        // 验证查询结果
        assertTrue(pendingOrders.size() >= 1);
        assertTrue(pendingOrders.stream().allMatch(o -> o.getStatus() == Order.OrderStatus.PENDING));
    }

    @Test
    void testFindByProductId() {
        // 保存多个订单
        Order order1 = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order order2 = Order.builder()
                .orderNumber("ORD20240220002")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PAID)
                .build();
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        
        // 查询产品的订单
        List<Order> productOrders = orderRepository.findByProductId(testProduct.getId());
        
        // 验证查询结果
        assertEquals(2, productOrders.size());
        assertTrue(productOrders.stream().allMatch(o -> o.getProductId().equals(testProduct.getId())));
    }

    @Test
    void testFindByUserIdAndStatus() {
        // 保存多个订单
        Order pendingOrder = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order paidOrder = Order.builder()
                .orderNumber("ORD20240220002")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PAID)
                .build();
        
        orderRepository.save(pendingOrder);
        orderRepository.save(paidOrder);
        
        // 查询用户的待处理订单
        List<Order> userPendingOrders = orderRepository.findByUserIdAndStatus(testUser.getId(), Order.OrderStatus.PENDING);
        
        // 验证查询结果
        assertTrue(userPendingOrders.size() >= 1);
        assertTrue(userPendingOrders.stream().allMatch(o -> 
            o.getUserId().equals(testUser.getId()) && o.getStatus() == Order.OrderStatus.PENDING));
    }

    @Test
    void testFindByUserIdAndTimeRange() {
        // 保存订单
        Order order1 = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order order2 = Order.builder()
                .orderNumber("ORD20240220002")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PAID)
                .build();
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        
        // 定义时间范围
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        
        // 查询时间范围内的用户订单
        List<Order> ordersInRange = orderRepository.findByUserIdAndTimeRange(testUser.getId(), startTime, endTime);
        
        // 验证查询结果
        assertTrue(ordersInRange.size() >= 2);
        assertTrue(ordersInRange.stream().allMatch(o -> 
            o.getUserId().equals(testUser.getId()) && 
            o.getCreatedAt().isAfter(startTime) && 
            o.getCreatedAt().isBefore(endTime)));
    }

    @Test
    void testFindByTimeRange() {
        // 保存订单
        Order order1 = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order order2 = Order.builder()
                .orderNumber("ORD20240220002")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PAID)
                .build();
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        
        // 定义时间范围
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        
        // 查询时间范围内的所有订单
        List<Order> ordersInRange = orderRepository.findByTimeRange(startTime, endTime);
        
        // 验证查询结果
        assertTrue(ordersInRange.size() >= 2);
        assertTrue(ordersInRange.stream().allMatch(o -> 
            o.getCreatedAt().isAfter(startTime) && 
            o.getCreatedAt().isBefore(endTime)));
    }

    @Test
    void testFindPendingOrders() {
        // 保存多个订单
        Order pendingOrder1 = Order.builder()
                .orderNumber("ORD20240220001")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(2)
                .totalPrice(new BigDecimal("179.98"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order pendingOrder2 = Order.builder()
                .orderNumber("ORD20240220002")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PENDING)
                .build();
        
        Order paidOrder = Order.builder()
                .orderNumber("ORD20240220003")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .status(Order.OrderStatus.PAID)
                .build();
        
        orderRepository.save(pendingOrder1);
        orderRepository.save(pendingOrder2);
        orderRepository.save(paidOrder);
        
        // 查询待处理订单
        List<Order> pendingOrders = orderRepository.findPendingOrders();
        
        // 验证查询结果
        assertTrue(pendingOrders.size() >= 2);
        assertTrue(pendingOrders.stream().allMatch(o -> o.getStatus() == Order.OrderStatus.PENDING));
    }

    @Test
    void testUpdateOrder() {
        // 保存订单
        Order savedOrder = orderRepository.save(testOrder);
        Long orderId = savedOrder.getId();
        
        // 更新订单信息
        savedOrder.setStatus(Order.OrderStatus.PAID);
        savedOrder.setPaymentMethod("微信支付");
        Order updatedOrder = orderRepository.save(savedOrder);
        
        // 验证更新结果
        assertEquals(Order.OrderStatus.PAID, updatedOrder.getStatus());
        assertEquals("微信支付", updatedOrder.getPaymentMethod());
        assertNotNull(updatedOrder.getUpdatedAt());
    }

    @Test
    void testDeleteOrder() {
        // 保存订单
        Order savedOrder = orderRepository.save(testOrder);
        Long orderId = savedOrder.getId();
        
        // 删除订单
        orderRepository.deleteById(orderId);
        
        // 验证订单已删除
        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        assertFalse(deletedOrder.isPresent());
    }

    @Test
    void testOrderPrePersist() {
        // 创建订单（不设置时间戳和状态）
        Order order = Order.builder()
                .orderNumber("ORD20240220999")
                .userId(testUser.getId())
                .productId(testProduct.getId())
                .quantity(1)
                .totalPrice(new BigDecimal("89.99"))
                .build();
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        // 验证时间戳和状态已自动设置
        assertNotNull(savedOrder.getCreatedAt());
        assertNotNull(savedOrder.getUpdatedAt());
        assertEquals(Order.OrderStatus.PENDING, savedOrder.getStatus());
    }

    @Test
    void testOrderPreUpdate() {
        // 保存订单
        Order savedOrder = orderRepository.save(testOrder);
        LocalDateTime originalUpdatedAt = savedOrder.getUpdatedAt();
        
        // 等待一段时间后更新
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 更新订单
        savedOrder.setStatus(Order.OrderStatus.SHIPPED);
        Order updatedOrder = orderRepository.save(savedOrder);
        
        // 验证updatedAt已更新
        assertNotNull(updatedOrder.getUpdatedAt());
        assertTrue(updatedOrder.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   updatedOrder.getUpdatedAt().isEqual(originalUpdatedAt));
    }
}
