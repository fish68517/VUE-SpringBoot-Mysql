package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.common.exception.BusinessException;
import com.xingluo.petshop.entity.Cart;
import com.xingluo.petshop.entity.Order;
import com.xingluo.petshop.entity.OrderItem;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.repository.CartRepository;
import com.xingluo.petshop.repository.OrderItemRepository;
import com.xingluo.petshop.repository.OrderRepository;
import com.xingluo.petshop.repository.ProductRepository;
import com.xingluo.petshop.service.OrderService;
import com.xingluo.petshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    
    private static final AtomicLong orderCounter = new AtomicLong(0);
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long counter = orderCounter.incrementAndGet() % 10000;
        return timestamp + String.format("%04d", counter);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, List<Long> cartIds, String receiverName, 
                            String receiverPhone, String receiverAddress, String remark) {
        // 查询购物车商品
        List<Cart> cartItems = cartRepository.findAllById(cartIds);
        if (cartItems.isEmpty()) {
            throw new BusinessException(400, "购物车为空");
        }
        
        // 验证购物车商品是否属于同一店铺
        Long shopId = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Cart cart : cartItems) {
            Product product = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new BusinessException(404, "商品不存在"));
            
            // 验证库存
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException(400, "商品 " + product.getName() + " 库存不足");
            }
            
            // 验证店铺
            if (shopId == null) {
                shopId = product.getShopId();
            } else if (!shopId.equals(product.getShopId())) {
                throw new BusinessException(400, "购物车商品必须来自同一店铺");
            }
            
            // 计算总金额
            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShopId(shopId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 待支付
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setRemark(remark);
        
        order = orderRepository.save(order);
        
        // 创建订单明细并扣减库存
        for (Cart cart : cartItems) {
            Product product = productRepository.findById(cart.getProductId()).get();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            
            orderItemRepository.save(orderItem);
            
            // 扣减库存（使用ProductService的方法）
            productService.deductStock(product.getId(), cart.getQuantity());
        }
        
        // 清空购物车
        cartRepository.deleteAllById(cartIds);
        
        return order;
    }
    
    @Override
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    @Override
    public Order getOrderDetail(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "订单状态不正确");
        }
        
        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException(400, "订单状态不允许取消");
        }
        
        // 恢复库存（使用ProductService的方法）
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            productService.restoreStock(item.getProductId(), item.getQuantity());
        }
        
        order.setStatus(4); // 已取消
        
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != 2) {
            throw new BusinessException(400, "订单未发货，无法确认收货");
        }
        
        order.setStatus(3); // 已完成
        
        // 增加商品销量
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException(404, "商品不存在"));
            product.setSales(product.getSales() + item.getQuantity());
            productRepository.save(product);
        }
        
        return orderRepository.save(order);
    }
    
    @Override
    public List<Order> getShopOrders(Long shopId) {
        return orderRepository.findByShopIdOrderByCreateTimeDesc(shopId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != 1) {
            throw new BusinessException(400, "订单状态不正确，无法发货");
        }
        
        order.setStatus(2); // 已发货
        order.setShipTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
}
