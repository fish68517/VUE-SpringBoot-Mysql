package com.agricultural.service;

import com.agricultural.entity.Order;
import com.agricultural.entity.AgriculturalProduct;
import com.agricultural.repository.OrderRepository;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单业务逻辑层
 * 处理订单相关的业务逻辑，包括创建订单、获取订单列表、更新订单状态、支付处理、发货处理等操作
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    /**
     * 创建订单
     * 创建新的订单记录，并自动扣减产品库存
     *
     * @param userId 用户ID
     * @param productId 产品ID
     * @param quantity 购买数量
     * @param deliveryAddress 送货地址
     * @return 创建的订单对象
     * @throws IllegalArgumentException 当参数无效或库存不足时抛出异常
     */
    public Order createOrder(Long userId, Long productId, Integer quantity, String deliveryAddress) {
        LoggerUtil.info("开始创建订单，用户ID: {}, 产品ID: {}, 购买数量: {}", userId, productId, quantity);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("创建订单失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        // 验证产品ID
        if (productId == null || productId <= 0) {
            LoggerUtil.warn("创建订单失败: 产品ID无效");
            throw new IllegalArgumentException("产品ID无效");
        }

        // 验证购买数量
        if (quantity == null || quantity <= 0) {
            LoggerUtil.warn("创建订单失败: 购买数量无效，数量: {}", quantity);
            throw new IllegalArgumentException("购买数量必须大于0");
        }

        // 验证送货地址
        if (StringUtil.isBlank(deliveryAddress)) {
            LoggerUtil.warn("创建订单失败: 送货地址为空");
            throw new IllegalArgumentException("送货地址不能为空");
        }

        // 获取产品信息
        AgriculturalProduct product = productService.getProductById(productId);

        // 检查库存是否充足
        if (!productService.isStockSufficient(productId, quantity)) {
            LoggerUtil.warn("创建订单失败: 库存不足，产品ID: {}, 所需数量: {}, 当前库存: {}", 
                    productId, quantity, product.getStock());
            throw new IllegalArgumentException("库存不足，无法完成订单");
        }

        // 计算订单总价
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));

        // 生成订单号
        String orderNumber = generateOrderNumber();

        // 创建订单对象
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .deliveryAddress(deliveryAddress)
                .build();

        // 保存订单
        Order savedOrder = orderRepository.save(order);
        LoggerUtil.info("订单创建成功，订单ID: {}, 订单号: {}, 用户ID: {}, 产品ID: {}, 数量: {}", 
                savedOrder.getId(), orderNumber, userId, productId, quantity);

        // 扣减产品库存
        try {
            productService.deductStock(productId, quantity);
            LoggerUtil.info("订单库存扣减成功，产品ID: {}, 扣减数量: {}", productId, quantity);
        } catch (Exception e) {
            LoggerUtil.error("订单库存扣减失败，订单ID: {}, 产品ID: {}, 错误信息: {}", 
                    savedOrder.getId(), productId, e.getMessage());
            // 删除已创建的订单
            orderRepository.deleteById(savedOrder.getId());
            throw new RuntimeException("订单创建失败: 库存扣减异常");
        }

        return savedOrder;
    }

    /**
     * 获取订单列表
     * 获取所有订单信息
     *
     * @return 订单列表
     */
    public List<Order> getOrderList() {
        LoggerUtil.info("获取订单列表");

        List<Order> orders = orderRepository.findAll();
        LoggerUtil.info("获取订单列表成功，订单总数: {}", orders.size());

        return orders;
    }

    /**
     * 根据订单ID获取订单详情
     * 获取指定ID的订单信息
     *
     * @param orderId 订单ID
     * @return 订单对象
     * @throws IllegalArgumentException 当订单不存在时抛出异常
     */
    public Order getOrderById(Long orderId) {
        LoggerUtil.info("根据ID获取订单详情，订单ID: {}", orderId);

        // 验证订单ID
        if (orderId == null || orderId <= 0) {
            LoggerUtil.warn("获取订单失败: 订单ID无效");
            throw new IllegalArgumentException("订单ID无效");
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            LoggerUtil.warn("获取订单失败: 订单不存在，订单ID: {}", orderId);
            throw new IllegalArgumentException("订单不存在");
        }

        LoggerUtil.info("根据ID获取订单详情成功，订单ID: {}", orderId);

        return orderOptional.get();
    }

    /**
     * 根据订单号获取订单详情
     * 获取指定订单号的订单信息
     *
     * @param orderNumber 订单号
     * @return 订单对象
     * @throws IllegalArgumentException 当订单不存在时抛出异常
     */
    public Order getOrderByOrderNumber(String orderNumber) {
        LoggerUtil.info("根据订单号获取订单详情，订单号: {}", orderNumber);

        // 验证订单号
        if (StringUtil.isBlank(orderNumber)) {
            LoggerUtil.warn("获取订单失败: 订单号为空");
            throw new IllegalArgumentException("订单号不能为空");
        }

        Optional<Order> orderOptional = orderRepository.findByOrderNumber(orderNumber);
        if (!orderOptional.isPresent()) {
            LoggerUtil.warn("获取订单失败: 订单不存在，订单号: {}", orderNumber);
            throw new IllegalArgumentException("订单不存在");
        }

        LoggerUtil.info("根据订单号获取订单详情成功，订单号: {}", orderNumber);

        return orderOptional.get();
    }

    /**
     * 获取用户订单列表
     * 获取指定用户的所有订单信息
     *
     * @param userId 用户ID
     * @return 订单列表
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public List<Order> getOrdersByUserId(Long userId) {
        LoggerUtil.info("获取用户订单列表，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取订单列表失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        List<Order> orders = orderRepository.findByUserId(userId);
        LoggerUtil.info("获取用户订单列表成功，用户ID: {}, 订单数: {}", userId, orders.size());

        return orders;
    }

    /**
     * 获取指定状态的订单列表
     * 获取所有指定状态的订单信息
     *
     * @param status 订单状态
     * @return 订单列表
     * @throws IllegalArgumentException 当状态为空时抛出异常
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        LoggerUtil.info("获取指定状态的订单列表，状态: {}", status);

        // 验证状态参数
        if (status == null) {
            LoggerUtil.warn("获取订单列表失败: 订单状态为空");
            throw new IllegalArgumentException("订单状态不能为空");
        }

        List<Order> orders = orderRepository.findByStatus(status);
        LoggerUtil.info("获取指定状态的订单列表成功，状态: {}, 订单数: {}", status, orders.size());

        return orders;
    }

    /**
     * 获取用户指定状态的订单列表
     * 获取指定用户的指定状态的订单信息
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<Order> getOrdersByUserIdAndStatus(Long userId, Order.OrderStatus status) {
        LoggerUtil.info("获取用户指定状态的订单列表，用户ID: {}, 状态: {}", userId, status);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取订单列表失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        // 验证状态参数
        if (status == null) {
            LoggerUtil.warn("获取订单列表失败: 订单状态为空");
            throw new IllegalArgumentException("订单状态不能为空");
        }

        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        LoggerUtil.info("获取用户指定状态的订单列表成功，用户ID: {}, 状态: {}, 订单数: {}", userId, status, orders.size());

        return orders;
    }

    /**
     * 获取时间范围内的用户订单列表
     * 获取指定用户在指定时间范围内的订单信息
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<Order> getOrdersByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LoggerUtil.info("获取时间范围内的用户订单列表，用户ID: {}, 开始时间: {}, 结束时间: {}", userId, startTime, endTime);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取订单列表失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        // 验证时间范围
        if (startTime == null || endTime == null) {
            LoggerUtil.warn("获取订单列表失败: 时间范围参数为空");
            throw new IllegalArgumentException("时间范围参数不能为空");
        }

        if (startTime.isAfter(endTime)) {
            LoggerUtil.warn("获取订单列表失败: 开始时间晚于结束时间");
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }

        List<Order> orders = orderRepository.findByUserIdAndTimeRange(userId, startTime, endTime);
        LoggerUtil.info("获取时间范围内的用户订单列表成功，用户ID: {}, 订单数: {}", userId, orders.size());

        return orders;
    }

    /**
     * 获取时间范围内的所有订单列表
     * 获取指定时间范围内的所有订单信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     * @throws IllegalArgumentException 当时间范围参数无效时抛出异常
     */
    public List<Order> getOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        LoggerUtil.info("获取时间范围内的所有订单列表，开始时间: {}, 结束时间: {}", startTime, endTime);

        // 验证时间范围
        if (startTime == null || endTime == null) {
            LoggerUtil.warn("获取订单列表失败: 时间范围参数为空");
            throw new IllegalArgumentException("时间范围参数不能为空");
        }

        if (startTime.isAfter(endTime)) {
            LoggerUtil.warn("获取订单列表失败: 开始时间晚于结束时间");
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }

        List<Order> orders = orderRepository.findByTimeRange(startTime, endTime);
        LoggerUtil.info("获取时间范围内的所有订单列表成功，订单数: {}", orders.size());

        return orders;
    }

    /**
     * 获取待处理订单列表
     * 获取所有状态为PENDING的订单
     *
     * @return 待处理订单列表
     */
    public List<Order> getPendingOrders() {
        LoggerUtil.info("获取待处理订单列表");

        List<Order> orders = orderRepository.findPendingOrders();
        LoggerUtil.info("获取待处理订单列表成功，订单数: {}", orders.size());

        return orders;
    }

    /**
     * 更新订单状态
     * 更新订单的状态信息
     *
     * @param orderId 订单ID
     * @param newStatus 新的订单状态
     * @return 更新后的订单对象
     * @throws IllegalArgumentException 当订单不存在或状态无效时抛出异常
     */
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        LoggerUtil.info("开始更新订单状态，订单ID: {}, 新状态: {}", orderId, newStatus);

        // 获取订单
        Order order = getOrderById(orderId);

        // 验证新状态
        if (newStatus == null) {
            LoggerUtil.warn("更新订单状态失败: 新状态为空，订单ID: {}", orderId);
            throw new IllegalArgumentException("订单状态不能为空");
        }

        // 记录旧状态
        Order.OrderStatus oldStatus = order.getStatus();

        // 更新状态
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        LoggerUtil.info("订单状态更新成功，订单ID: {}, 旧状态: {}, 新状态: {}", orderId, oldStatus, newStatus);

        return updatedOrder;
    }

    /**
     * 处理订单支付
     * 处理订单的支付逻辑，更新订单状态为已支付
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 更新后的订单对象
     * @throws IllegalArgumentException 当订单不存在或支付方式无效时抛出异常
     */
    public Order processPayment(Long orderId, String paymentMethod) {
        LoggerUtil.info("开始处理订单支付，订单ID: {}, 支付方式: {}", orderId, paymentMethod);

        // 获取订单
        Order order = getOrderById(orderId);

        // 验证支付方式
        if (StringUtil.isBlank(paymentMethod)) {
            LoggerUtil.warn("处理支付失败: 支付方式为空，订单ID: {}", orderId);
            throw new IllegalArgumentException("支付方式不能为空");
        }

        // 验证订单状态是否可以支付
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            LoggerUtil.warn("处理支付失败: 订单状态不是待处理，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            throw new IllegalArgumentException("只有待处理的订单才能进行支付");
        }

        // 更新支付方式
        order.setPaymentMethod(paymentMethod);

        // 更新订单状态为已支付
        order.setStatus(Order.OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order);

        LoggerUtil.info("订单支付处理成功，订单ID: {}, 支付方式: {}", orderId, paymentMethod);

        return updatedOrder;
    }

    /**
     * 处理订单发货
     * 处理订单的发货逻辑，更新订单状态为已发货
     *
     * @param orderId 订单ID
     * @return 更新后的订单对象
     * @throws IllegalArgumentException 当订单不存在或订单状态不允许发货时抛出异常
     */
    public Order processShipment(Long orderId) {
        LoggerUtil.info("开始处理订单发货，订单ID: {}", orderId);

        // 获取订单
        Order order = getOrderById(orderId);

        // 验证订单状态是否可以发货
        if (order.getStatus() != Order.OrderStatus.PAID) {
            LoggerUtil.warn("处理发货失败: 订单状态不是已支付，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            throw new IllegalArgumentException("只有已支付的订单才能进行发货");
        }

        // 更新订单状态为已发货
        order.setStatus(Order.OrderStatus.SHIPPED);
        Order updatedOrder = orderRepository.save(order);

        LoggerUtil.info("订单发货处理成功，订单ID: {}", orderId);

        return updatedOrder;
    }

    /**
     * 确认订单送达
     * 处理订单的送达确认逻辑，更新订单状态为已送达
     *
     * @param orderId 订单ID
     * @return 更新后的订单对象
     * @throws IllegalArgumentException 当订单不存在或订单状态不允许确认送达时抛出异常
     */
    public Order confirmDelivery(Long orderId) {
        LoggerUtil.info("开始确认订单送达，订单ID: {}", orderId);

        // 获取订单
        Order order = getOrderById(orderId);

        // 验证订单状态是否可以确认送达
        if (order.getStatus() != Order.OrderStatus.SHIPPED) {
            LoggerUtil.warn("确认送达失败: 订单状态不是已发货，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            throw new IllegalArgumentException("只有已发货的订单才能确认送达");
        }

        // 更新订单状态为已送达
        order.setStatus(Order.OrderStatus.DELIVERED);
        Order updatedOrder = orderRepository.save(order);

        LoggerUtil.info("订单送达确认成功，订单ID: {}", orderId);

        return updatedOrder;
    }

    /**
     * 取消订单
     * 取消订单并恢复产品库存
     *
     * @param orderId 订单ID
     * @return 更新后的订单对象
     * @throws IllegalArgumentException 当订单不存在或订单状态不允许取消时抛出异常
     */
    public Order cancelOrder(Long orderId) {
        LoggerUtil.info("开始取消订单，订单ID: {}", orderId);

        // 获取订单
        Order order = getOrderById(orderId);

        // 验证订单状态是否可以取消
        if (order.getStatus() == Order.OrderStatus.SHIPPED || 
            order.getStatus() == Order.OrderStatus.DELIVERED || 
            order.getStatus() == Order.OrderStatus.CANCELLED) {
            LoggerUtil.warn("取消订单失败: 订单状态不允许取消，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            throw new IllegalArgumentException("该订单状态不允许取消");
        }

        // 恢复产品库存
        try {
            productService.increaseStock(order.getProductId(), order.getQuantity());
            LoggerUtil.info("订单库存恢复成功，产品ID: {}, 恢复数量: {}", order.getProductId(), order.getQuantity());
        } catch (Exception e) {
            LoggerUtil.error("订单库存恢复失败，订单ID: {}, 产品ID: {}, 错误信息: {}", 
                    orderId, order.getProductId(), e.getMessage());
            throw new RuntimeException("订单取消失败: 库存恢复异常");
        }

        // 更新订单状态为已取消
        order.setStatus(Order.OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        LoggerUtil.info("订单取消成功，订单ID: {}", orderId);

        return updatedOrder;
    }

    /**
     * 获取订单总数
     * 获取系统中所有订单的总数
     *
     * @return 订单总数
     */
    public long getOrderCount() {
        LoggerUtil.info("获取订单总数");

        long count = orderRepository.count();
        LoggerUtil.info("获取订单总数成功，订单总数: {}", count);

        return count;
    }

    /**
     * 获取用户订单总数
     * 获取指定用户的订单数量
     *
     * @param userId 用户ID
     * @return 订单总数
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public long getOrderCountByUserId(Long userId) {
        LoggerUtil.info("获取用户订单总数，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取订单总数失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        long count = getOrdersByUserId(userId).size();
        LoggerUtil.info("获取用户订单总数成功，用户ID: {}, 订单总数: {}", userId, count);

        return count;
    }

    /**
     * 生成订单号
     * 生成唯一的订单号，格式为: ORD + 时间戳 + 随机数
     *
     * @return 生成的订单号
     */
    private String generateOrderNumber() {
        // 使用时间戳和UUID生成唯一的订单号
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String orderNumber = "ORD" + timestamp + uuid;
        
        LoggerUtil.info("生成订单号: {}", orderNumber);
        
        return orderNumber;
    }

    /**
     * 检查订单是否存在
     * 用于验证订单是否存在
     *
     * @param orderId 订单ID
     * @return 如果订单存在返回true，否则返回false
     */
    public boolean orderExists(Long orderId) {
        LoggerUtil.info("检查订单是否存在，订单ID: {}", orderId);

        if (orderId == null || orderId <= 0) {
            return false;
        }

        return orderRepository.existsById(orderId);
    }

    /**
     * 获取用户待支付订单数
     * 获取指定用户的待支付订单数量
     *
     * @param userId 用户ID
     * @return 待支付订单数
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public long getPendingPaymentOrderCount(Long userId) {
        LoggerUtil.info("获取用户待支付订单数，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取待支付订单数失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        long count = getOrdersByUserIdAndStatus(userId, Order.OrderStatus.PENDING).size();
        LoggerUtil.info("获取用户待支付订单数成功，用户ID: {}, 待支付订单数: {}", userId, count);

        return count;
    }

    /**
     * 获取用户已支付订单数
     * 获取指定用户的已支付订单数量
     *
     * @param userId 用户ID
     * @return 已支付订单数
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public long getPaidOrderCount(Long userId) {
        LoggerUtil.info("获取用户已支付订单数，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取已支付订单数失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        long count = getOrdersByUserIdAndStatus(userId, Order.OrderStatus.PAID).size();
        LoggerUtil.info("获取用户已支付订单数成功，用户ID: {}, 已支付订单数: {}", userId, count);

        return count;
    }

    /**
     * 获取用户已发货订单数
     * 获取指定用户的已发货订单数量
     *
     * @param userId 用户ID
     * @return 已发货订单数
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public long getShippedOrderCount(Long userId) {
        LoggerUtil.info("获取用户已发货订单数，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取已发货订单数失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        long count = getOrdersByUserIdAndStatus(userId, Order.OrderStatus.SHIPPED).size();
        LoggerUtil.info("获取用户已发货订单数成功，用户ID: {}, 已发货订单数: {}", userId, count);

        return count;
    }

    /**
     * 获取用户已送达订单数
     * 获取指定用户的已送达订单数量
     *
     * @param userId 用户ID
     * @return 已送达订单数
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public long getDeliveredOrderCount(Long userId) {
        LoggerUtil.info("获取用户已送达订单数，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取已送达订单数失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        long count = getOrdersByUserIdAndStatus(userId, Order.OrderStatus.DELIVERED).size();
        LoggerUtil.info("获取用户已送达订单数成功，用户ID: {}, 已送达订单数: {}", userId, count);

        return count;
    }
}
