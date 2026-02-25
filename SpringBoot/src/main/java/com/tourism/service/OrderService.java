package com.tourism.service;

import com.tourism.entity.Order;
import com.tourism.entity.OrderItem;
import com.tourism.entity.Attraction;
import com.tourism.entity.Hotel;
import com.tourism.entity.HotelRoom;
import com.tourism.entity.Product;
import com.tourism.exception.BusinessException;
import com.tourism.repository.OrderRepository;
import com.tourism.repository.OrderItemRepository;
import com.tourism.repository.AttractionRepository;
import com.tourism.repository.HotelRepository;
import com.tourism.repository.HotelRoomRepository;
import com.tourism.repository.ProductRepository;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单业务逻辑服务
 */
@Service
public class OrderService {
    
    private static final Logger logger = LoggerUtil.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private AttractionRepository attractionRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private HotelRoomRepository hotelRoomRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNumber() {
        // 格式: ORD + 时间戳 + 随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD" + timestamp + randomPart;
    }
    
    /**
     * 创建景点门票订单
     * @param userId 用户ID
     * @param attractionId 景点ID
     * @param quantity 购买数量
     * @param visitDate 访问日期（可选）
     * @return 创建的订单对象
     */
    @Transactional
    public Order createAttractionOrder(Long userId, Long attractionId, Integer quantity, String visitDate) {
        // 验证参数
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "创建景点订单失败：无效的用户ID - " + userId);
            throw new BusinessException("无效的用户ID");
        }
        
        if (attractionId == null || attractionId <= 0) {
            LoggerUtil.warn(logger, "创建景点订单失败：无效的景点ID - " + attractionId);
            throw new BusinessException("无效的景点ID");
        }
        
        if (quantity == null || quantity <= 0) {
            LoggerUtil.warn(logger, "创建景点订单失败：无效的购买数量 - " + quantity);
            throw new BusinessException("购买数量必须大于0");
        }
        
        // 查询景点信息
        Optional<Attraction> attractionOptional = attractionRepository.findById(attractionId);
        if (!attractionOptional.isPresent()) {
            LoggerUtil.warn(logger, "创建景点订单失败：景点不存在 - ID: " + attractionId);
            throw new BusinessException("景点不存在");
        }
        
        Attraction attraction = attractionOptional.get();
        
        // 计算订单总价
        BigDecimal unitPrice = attraction.getTicketPrice();
        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(quantity));
        
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("pending");
        order.setOrderType("attraction");
        
        Order savedOrder = orderRepository.save(order);
        LoggerUtil.info(logger, "创建景点订单成功 - 订单号: " + order.getOrderNumber() + ", 用户ID: " + userId);
        
        // 创建订单项目
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(savedOrder.getId());
        orderItem.setItemType("attraction");
        orderItem.setItemId(attractionId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setSubtotal(totalPrice);
        
        orderItemRepository.save(orderItem);
        LoggerUtil.info(logger, "创建订单项目成功 - 订单ID: " + savedOrder.getId() + ", 景点ID: " + attractionId);
        
        return savedOrder;
    }
    
    /**
     * 创建酒店预订订单
     * @param userId 用户ID
     * @param hotelId 酒店ID
     * @param roomType 房间类型
     * @param checkInDate 入住日期
     * @param checkOutDate 退房日期
     * @param quantity 房间数量
     * @return 创建的订单对象
     */
    @Transactional
    public Order createHotelOrder(Long userId, Long hotelId, String roomType, String checkInDate, String checkOutDate, Integer quantity) {
        // 验证参数
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "创建酒店订单失败：无效的用户ID - " + userId);
            throw new BusinessException("无效的用户ID");
        }
        
        if (hotelId == null || hotelId <= 0) {
            LoggerUtil.warn(logger, "创建酒店订单失败：无效的酒店ID - " + hotelId);
            throw new BusinessException("无效的酒店ID");
        }
        
        if (roomType == null || roomType.trim().isEmpty()) {
            LoggerUtil.warn(logger, "创建酒店订单失败：房间类型不能为空");
            throw new BusinessException("房间类型不能为空");
        }
        
        if (checkInDate == null || checkInDate.trim().isEmpty()) {
            LoggerUtil.warn(logger, "创建酒店订单失败：入住日期不能为空");
            throw new BusinessException("入住日期不能为空");
        }
        
        if (checkOutDate == null || checkOutDate.trim().isEmpty()) {
            LoggerUtil.warn(logger, "创建酒店订单失败：退房日期不能为空");
            throw new BusinessException("退房日期不能为空");
        }
        
        if (quantity == null || quantity <= 0) {
            LoggerUtil.warn(logger, "创建酒店订单失败：无效的房间数量 - " + quantity);
            throw new BusinessException("房间数量必须大于0");
        }
        
        // 查询酒店信息
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (!hotelOptional.isPresent()) {
            LoggerUtil.warn(logger, "创建酒店订单失败：酒店不存在 - ID: " + hotelId);
            throw new BusinessException("酒店不存在");
        }
        
        // 查询房间信息
        Optional<HotelRoom> roomOptional = hotelRoomRepository.findByHotelIdAndRoomType(hotelId, roomType);
        if (!roomOptional.isPresent()) {
            LoggerUtil.warn(logger, "创建酒店订单失败：房间类型不存在 - 酒店ID: " + hotelId + ", 房间类型: " + roomType);
            throw new BusinessException("房间类型不存在");
        }
        
        HotelRoom room = roomOptional.get();
        
        // 检查房间可用性
        if (room.getQuantity() < quantity) {
            LoggerUtil.warn(logger, "创建酒店订单失败：房间数量不足 - 需要: " + quantity + ", 可用: " + room.getQuantity());
            throw new BusinessException("房间数量不足，可用房间数: " + room.getQuantity());
        }
        
        // 计算订单总价（简化计算：按1晚计算）
        BigDecimal pricePerNight = room.getPricePerNight();
        BigDecimal totalPrice = pricePerNight.multiply(new BigDecimal(quantity));
        
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("pending");
        order.setOrderType("hotel");
        
        Order savedOrder = orderRepository.save(order);
        LoggerUtil.info(logger, "创建酒店订单成功 - 订单号: " + order.getOrderNumber() + ", 用户ID: " + userId);
        
        // 创建订单项目
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(savedOrder.getId());
        orderItem.setItemType("hotel");
        orderItem.setItemId(hotelId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(pricePerNight);
        orderItem.setSubtotal(totalPrice);
        
        orderItemRepository.save(orderItem);
        LoggerUtil.info(logger, "创建订单项目成功 - 订单ID: " + savedOrder.getId() + ", 酒店ID: " + hotelId);
        
        return savedOrder;
    }
    
    /**
     * 创建商品购买订单
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 购买数量
     * @return 创建的订单对象
     */
    @Transactional
    public Order createProductOrder(Long userId, Long productId, Integer quantity) {
        // 验证参数
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "创建商品订单失败：无效的用户ID - " + userId);
            throw new BusinessException("无效的用户ID");
        }
        
        if (productId == null || productId <= 0) {
            LoggerUtil.warn(logger, "创建商品订单失败：无效的商品ID - " + productId);
            throw new BusinessException("无效的商品ID");
        }
        
        if (quantity == null || quantity <= 0) {
            LoggerUtil.warn(logger, "创建商品订单失败：无效的购买数量 - " + quantity);
            throw new BusinessException("购买数量必须大于0");
        }
        
        // 查询商品信息
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            LoggerUtil.warn(logger, "创建商品订单失败：商品不存在 - ID: " + productId);
            throw new BusinessException("商品不存在");
        }
        
        Product product = productOptional.get();
        
        // 检查库存
        if (product.getStock() < quantity) {
            LoggerUtil.warn(logger, "创建商品订单失败：库存不足 - 需要: " + quantity + ", 可用: " + product.getStock());
            throw new BusinessException("库存不足，可用库存: " + product.getStock());
        }
        
        // 计算订单总价
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(quantity));
        
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("pending");
        order.setOrderType("product");
        
        Order savedOrder = orderRepository.save(order);
        LoggerUtil.info(logger, "创建商品订单成功 - 订单号: " + order.getOrderNumber() + ", 用户ID: " + userId);
        
        // 创建订单项目
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(savedOrder.getId());
        orderItem.setItemType("product");
        orderItem.setItemId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setSubtotal(totalPrice);
        
        orderItemRepository.save(orderItem);
        LoggerUtil.info(logger, "创建订单项目成功 - 订单ID: " + savedOrder.getId() + ", 商品ID: " + productId);
        
        // 扣减库存
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        LoggerUtil.info(logger, "扣减商品库存成功 - 商品ID: " + productId + ", 扣减数量: " + quantity + ", 剩余库存: " + product.getStock());
        
        return savedOrder;
    }
    
    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单对象
     */
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    /**
     * 获取用户订单列表（分页）
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 订单分页数据
     */
    public Page<Order> getUserOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        LoggerUtil.info(logger, "获取用户订单列表成功 - 用户ID: " + userId + ", 页码: " + page);
        return orders;
    }
    
    /**
     * 获取用户指定状态的订单列表（分页）
     * @param userId 用户ID
     * @param status 订单状态
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 订单分页数据
     */
    public Page<Order> getUserOrdersByStatus(Long userId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        LoggerUtil.info(logger, "获取用户指定状态订单列表成功 - 用户ID: " + userId + ", 状态: " + status);
        return orders;
    }
    
    /**
     * 获取订单详情（包含订单项目）
     * @param orderId 订单ID
     * @return 包含订单项目的订单信息
     */
    public Order getOrderDetail(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            LoggerUtil.warn(logger, "获取订单详情失败：订单不存在 - ID: " + orderId);
            throw new BusinessException("订单不存在");
        }
        
        LoggerUtil.info(logger, "获取订单详情成功 - 订单ID: " + orderId);
        return orderOptional.get();
    }
    
    /**
     * 获取订单项目列表
     * @param orderId 订单ID
     * @return 订单项目列表
     */
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 更新后的订单对象
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            LoggerUtil.warn(logger, "取消订单失败：订单不存在 - ID: " + orderId);
            throw new BusinessException("订单不存在");
        }
        
        Order order = orderOptional.get();
        
        // 检查订单状态
        if ("cancelled".equals(order.getStatus())) {
            LoggerUtil.warn(logger, "取消订单失败：订单已被取消 - ID: " + orderId);
            throw new BusinessException("订单已被取消");
        }
        
        if ("completed".equals(order.getStatus())) {
            LoggerUtil.warn(logger, "取消订单失败：已完成的订单无法取消 - ID: " + orderId);
            throw new BusinessException("已完成的订单无法取消");
        }
        
        order.setStatus("cancelled");
        Order updatedOrder = orderRepository.save(order);
        LoggerUtil.info(logger, "取消订单成功 - 订单ID: " + orderId + ", 订单号: " + order.getOrderNumber());
        
        return updatedOrder;
    }
    
    /**
     * 更新订单状态（管理员）
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新后的订单对象
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新订单状态失败：订单不存在 - ID: " + orderId);
            throw new BusinessException("订单不存在");
        }
        
        // 验证状态值
        if (!isValidStatus(status)) {
            LoggerUtil.warn(logger, "更新订单状态失败：无效的状态 - " + status);
            throw new BusinessException("无效的订单状态");
        }
        
        Order order = orderOptional.get();
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        LoggerUtil.info(logger, "更新订单状态成功 - 订单ID: " + orderId + ", 新状态: " + status);
        
        return updatedOrder;
    }
    
    /**
     * 验证订单状态是否有效
     * @param status 状态值
     * @return 是否有效
     */
    private boolean isValidStatus(String status) {
        return "pending".equals(status) || "confirmed".equals(status) || 
               "completed".equals(status) || "cancelled".equals(status);
    }
    
    /**
     * 获取所有订单列表（分页，管理员）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 订单分页数据
     */
    public Page<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(pageable);
        LoggerUtil.info(logger, "获取所有订单列表成功 - 页码: " + page);
        return orders;
    }
    
    /**
     * 获取指定状态的订单列表（分页，管理员）
     * @param status 订单状态
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 订单分页数据
     */
    public Page<Order> getOrdersByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        LoggerUtil.info(logger, "获取指定状态订单列表成功 - 状态: " + status + ", 页码: " + page);
        return orders;
    }
    
    /**
     * 获取指定类型的订单列表（分页，管理员）
     * @param orderType 订单类型
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 订单分页数据
     */
    public Page<Order> getOrdersByType(String orderType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByOrderType(orderType, pageable);
        LoggerUtil.info(logger, "获取指定类型订单列表成功 - 类型: " + orderType + ", 页码: " + page);
        return orders;
    }
    
    /**
     * 获取订单统计信息
     * @return 包含各种统计数据的Map
     */
    public java.util.Map<String, Object> getOrderStatistics() {
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        
        // 总订单数
        long totalOrders = orderRepository.count();
        statistics.put("totalOrders", totalOrders);
        
        // 各状态订单数
        long pendingOrders = orderRepository.countByStatus("pending");
        long confirmedOrders = orderRepository.countByStatus("confirmed");
        long completedOrders = orderRepository.countByStatus("completed");
        long cancelledOrders = orderRepository.countByStatus("cancelled");
        
        java.util.Map<String, Long> statusStats = new java.util.HashMap<>();
        statusStats.put("pending", pendingOrders);
        statusStats.put("confirmed", confirmedOrders);
        statusStats.put("completed", completedOrders);
        statusStats.put("cancelled", cancelledOrders);
        statistics.put("statusStats", statusStats);
        
        // 各类型订单数
        long attractionOrders = orderRepository.countByOrderType("attraction");
        long hotelOrders = orderRepository.countByOrderType("hotel");
        long productOrders = orderRepository.countByOrderType("product");
        
        java.util.Map<String, Long> typeStats = new java.util.HashMap<>();
        typeStats.put("attraction", attractionOrders);
        typeStats.put("hotel", hotelOrders);
        typeStats.put("product", productOrders);
        statistics.put("typeStats", typeStats);
        
        // 总收入（所有已完成订单的总价）
        BigDecimal totalRevenue = orderRepository.sumTotalPriceByStatus("completed");
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        statistics.put("totalRevenue", totalRevenue);
        
        // 平均订单金额
        BigDecimal averageOrderPrice = BigDecimal.ZERO;
        if (totalOrders > 0) {
            BigDecimal totalPrice = orderRepository.sumTotalPrice();
            if (totalPrice != null) {
                averageOrderPrice = totalPrice.divide(new BigDecimal(totalOrders), 2, java.math.RoundingMode.HALF_UP);
            }
        }
        statistics.put("averageOrderPrice", averageOrderPrice);
        
        LoggerUtil.info(logger, "获取订单统计信息成功 - 总订单数: " + totalOrders + ", 总收入: " + totalRevenue);
        
        return statistics;
    }
}
