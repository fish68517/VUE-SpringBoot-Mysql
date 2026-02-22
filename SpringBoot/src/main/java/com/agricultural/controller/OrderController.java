package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.Order;
import com.agricultural.service.OrderService;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制层
 * 
 * 处理订单相关的HTTP请求，包括创建订单、查询订单、更新订单状态、支付处理等操作
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单接口
     * 
     * @param userId 用户ID
     * @param productId 产品ID
     * @param quantity 购买数量
     * @param deliveryAddress 送货地址
     * @return 创建成功的订单信息
     */
    @PostMapping
    public Result<Order> createOrder(
            @RequestParam @NotNull(message = "用户ID不能为空") Long userId,
            @RequestParam @NotNull(message = "产品ID不能为空") Long productId,
            @RequestParam @NotNull(message = "购买数量不能为空") Integer quantity,
            @RequestParam @NotBlank(message = "送货地址不能为空") String deliveryAddress) {
        
        LoggerUtil.info("收到创建订单请求，用户ID: {}, 产品ID: {}, 数量: {}", userId, productId, quantity);
        
        try {
            // 参数验证
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("创建订单请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            if (productId == null || productId <= 0) {
                LoggerUtil.warn("创建订单请求参数验证失败: 产品ID无效");
                return Result.validationError("产品ID无效");
            }
            
            if (quantity == null || quantity <= 0) {
                LoggerUtil.warn("创建订单请求参数验证失败: 购买数量无效");
                return Result.validationError("购买数量必须大于0");
            }
            
            if (StringUtil.isBlank(deliveryAddress)) {
                LoggerUtil.warn("创建订单请求参数验证失败: 送货地址为空");
                return Result.validationError("送货地址不能为空");
            }
            
            // 调用业务层创建订单
            Order createdOrder = orderService.createOrder(userId, productId, quantity, deliveryAddress);
            
            LoggerUtil.info("创建订单成功，订单ID: {}, 订单号: {}", createdOrder.getId(), createdOrder.getOrderNumber());
            return Result.success("订单创建成功", createdOrder);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("创建订单失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("创建订单异常: " + e.getMessage(), e);
            return Result.error("创建订单失败，请稍后重试");
        }
    }

    /**
     * 获取订单列表接口
     * 
     * @return 所有订单列表
     */
    @GetMapping
    public Result<List<Order>> getOrderList() {
        
        LoggerUtil.info("收到获取订单列表请求");
        
        try {
            // 调用业务层获取订单列表
            List<Order> orders = orderService.getOrderList();
            
            LoggerUtil.info("获取订单列表成功，订单总数: {}", orders.size());
            return Result.success(orders);
            
        } catch (Exception e) {
            LoggerUtil.error("获取订单列表异常: " + e.getMessage(), e);
            return Result.error("获取订单列表失败，请稍后重试");
        }
    }

    /**
     * 获取订单详情接口
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(
            @PathVariable @NotNull(message = "订单ID不能为空") Long id) {
        
        LoggerUtil.info("收到获取订单详情请求，订单ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("获取订单详情请求参数验证失败: 订单ID无效");
                return Result.validationError("订单ID无效");
            }
            
            // 调用业务层获取订单详情
            Order order = orderService.getOrderById(id);
            
            LoggerUtil.info("获取订单详情成功，订单ID: {}", id);
            return Result.success(order);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取订单详情失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取订单详情异常: " + e.getMessage(), e);
            return Result.error("获取订单详情失败，请稍后重试");
        }
    }

    /**
     * 获取用户订单列表接口
     * 
     * @param userId 用户ID
     * @return 用户的订单列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Order>> getOrdersByUserId(
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        
        LoggerUtil.info("收到获取用户订单列表请求，用户ID: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("获取用户订单列表请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层获取用户订单列表
            List<Order> orders = orderService.getOrdersByUserId(userId);
            
            LoggerUtil.info("获取用户订单列表成功，用户ID: {}, 订单数: {}", userId, orders.size());
            return Result.success(orders);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取用户订单列表失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取用户订单列表异常: " + e.getMessage(), e);
            return Result.error("获取用户订单列表失败，请稍后重试");
        }
    }

    /**
     * 获取指定状态的订单列表接口
     * 
     * @param status 订单状态
     * @return 指定状态的订单列表
     */
    @GetMapping("/status/{status}")
    public Result<List<Order>> getOrdersByStatus(
            @PathVariable @NotBlank(message = "订单状态不能为空") String status) {
        
        LoggerUtil.info("收到获取指定状态订单列表请求，状态: {}", status);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(status)) {
                LoggerUtil.warn("获取指定状态订单列表请求参数验证失败: 状态为空");
                return Result.validationError("订单状态不能为空");
            }
            
            // 将字符串转换为枚举
            Order.OrderStatus orderStatus;
            try {
                orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                LoggerUtil.warn("获取指定状态订单列表失败: 无效的订单状态 {}", status);
                return Result.validationError("无效的订单状态: " + status);
            }
            
            // 调用业务层获取指定状态的订单列表
            List<Order> orders = orderService.getOrdersByStatus(orderStatus);
            
            LoggerUtil.info("获取指定状态订单列表成功，状态: {}, 订单数: {}", status, orders.size());
            return Result.success(orders);
            
        } catch (Exception e) {
            LoggerUtil.error("获取指定状态订单列表异常: " + e.getMessage(), e);
            return Result.error("获取指定状态订单列表失败，请稍后重试");
        }
    }

    /**
     * 更新订单状态接口
     * 
     * @param id 订单ID
     * @param status 新的订单状态
     * @return 更新后的订单信息
     */
    @PutMapping("/{id}/status")
    public Result<Order> updateOrderStatus(
            @PathVariable @NotNull(message = "订单ID不能为空") Long id,
            @RequestParam @NotBlank(message = "订单状态不能为空") String status) {
        
        LoggerUtil.info("收到更新订单状态请求，订单ID: {}, 新状态: {}", id, status);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("更新订单状态请求参数验证失败: 订单ID无效");
                return Result.validationError("订单ID无效");
            }
            
            if (StringUtil.isBlank(status)) {
                LoggerUtil.warn("更新订单状态请求参数验证失败: 状态为空");
                return Result.validationError("订单状态不能为空");
            }
            
            // 将字符串转换为枚举
            Order.OrderStatus newStatus;
            try {
                newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                LoggerUtil.warn("更新订单状态失败: 无效的订单状态 {}", status);
                return Result.validationError("无效的订单状态: " + status);
            }
            
            // 调用业务层更新订单状态
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
            
            LoggerUtil.info("更新订单状态成功，订单ID: {}, 新状态: {}", id, status);
            return Result.success("订单状态更新成功", updatedOrder);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("更新订单状态失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("更新订单状态异常: " + e.getMessage(), e);
            return Result.error("更新订单状态失败，请稍后重试");
        }
    }

    /**
     * 订单支付接口
     * 
     * @param id 订单ID
     * @param paymentMethod 支付方式
     * @return 支付后的订单信息
     */
    @PostMapping("/{id}/pay")
    public Result<Order> processPayment(
            @PathVariable @NotNull(message = "订单ID不能为空") Long id,
            @RequestParam @NotBlank(message = "支付方式不能为空") String paymentMethod) {
        
        LoggerUtil.info("收到订单支付请求，订单ID: {}, 支付方式: {}", id, paymentMethod);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("订单支付请求参数验证失败: 订单ID无效");
                return Result.validationError("订单ID无效");
            }
            
            if (StringUtil.isBlank(paymentMethod)) {
                LoggerUtil.warn("订单支付请求参数验证失败: 支付方式为空");
                return Result.validationError("支付方式不能为空");
            }
            
            // 调用业务层处理支付
            Order paidOrder = orderService.processPayment(id, paymentMethod);
            
            LoggerUtil.info("订单支付成功，订单ID: {}, 支付方式: {}", id, paymentMethod);
            return Result.success("订单支付成功", paidOrder);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("订单支付失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("订单支付异常: " + e.getMessage(), e);
            return Result.error("订单支付失败，请稍后重试");
        }
    }

    /**
     * 订单发货接口
     * 
     * @param id 订单ID
     * @return 发货后的订单信息
     */
    @PostMapping("/{id}/ship")
    public Result<Order> processShipment(
            @PathVariable @NotNull(message = "订单ID不能为空") Long id) {
        
        LoggerUtil.info("收到订单发货请求，订单ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("订单发货请求参数验证失败: 订单ID无效");
                return Result.validationError("订单ID无效");
            }
            
            // 调用业务层处理发货
            Order shippedOrder = orderService.processShipment(id);
            
            LoggerUtil.info("订单发货成功，订单ID: {}", id);
            return Result.success("订单发货成功", shippedOrder);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("订单发货失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("订单发货异常: " + e.getMessage(), e);
            return Result.error("订单发货失败，请稍后重试");
        }
    }

    /**
     * 确认订单送达接口
     * 
     * @param id 订单ID
     * @return 送达后的订单信息
     */
    @PostMapping("/{id}/confirm-delivery")
    public Result<Order> confirmDelivery(
            @PathVariable @NotNull(message = "订单ID不能为空") Long id) {
        
        LoggerUtil.info("收到确认订单送达请求，订单ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("确认订单送达请求参数验证失败: 订单ID无效");
                return Result.validationError("订单ID无效");
            }
            
            // 调用业务层确认送达
            Order deliveredOrder = orderService.confirmDelivery(id);
            
            LoggerUtil.info("确认订单送达成功，订单ID: {}", id);
            return Result.success("订单送达确认成功", deliveredOrder);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("确认订单送达失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("确认订单送达异常: " + e.getMessage(), e);
            return Result.error("确认订单送达失败，请稍后重试");
        }
    }

    /**
     * 取消订单接口
     * 
     * @param id 订单ID
     * @return 取消后的订单信息
     */
    @PostMapping("/{id}/cancel")
    public Result<Order> cancelOrder(
            @PathVariable @NotNull(message = "订单ID不能为空") Long id) {
        
        LoggerUtil.info("收到取消订单请求，订单ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("取消订单请求参数验证失败: 订单ID无效");
                return Result.validationError("订单ID无效");
            }
            
            // 调用业务层取消订单
            Order cancelledOrder = orderService.cancelOrder(id);
            
            LoggerUtil.info("取消订单成功，订单ID: {}", id);
            return Result.success("订单取消成功", cancelledOrder);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("取消订单失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("取消订单异常: " + e.getMessage(), e);
            return Result.error("取消订单失败，请稍后重试");
        }
    }

    /**
     * 获取订单总数接口
     * 
     * @return 订单总数
     */
    @GetMapping("/count")
    public Result<Long> getOrderCount() {
        
        LoggerUtil.info("收到获取订单总数请求");
        
        try {
            // 调用业务层获取订单总数
            long count = orderService.getOrderCount();
            
            LoggerUtil.info("获取订单总数成功，订单总数: {}", count);
            return Result.success(count);
            
        } catch (Exception e) {
            LoggerUtil.error("获取订单总数异常: " + e.getMessage(), e);
            return Result.error("获取订单总数失败，请稍后重试");
        }
    }

    /**
     * 获取用户订单总数接口
     * 
     * @param userId 用户ID
     * @return 用户的订单总数
     */
    @GetMapping("/user/{userId}/count")
    public Result<Long> getOrderCountByUserId(
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        
        LoggerUtil.info("收到获取用户订单总数请求，用户ID: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("获取用户订单总数请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层获取用户订单总数
            long count = orderService.getOrderCountByUserId(userId);
            
            LoggerUtil.info("获取用户订单总数成功，用户ID: {}, 订单总数: {}", userId, count);
            return Result.success(count);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取用户订单总数失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取用户订单总数异常: " + e.getMessage(), e);
            return Result.error("获取用户订单总数失败，请稍后重试");
        }
    }

    /**
     * 获取待处理订单列表接口
     * 
     * @return 待处理订单列表
     */
    @GetMapping("/pending")
    public Result<List<Order>> getPendingOrders() {
        
        LoggerUtil.info("收到获取待处理订单列表请求");
        
        try {
            // 调用业务层获取待处理订单列表
            List<Order> orders = orderService.getPendingOrders();
            
            LoggerUtil.info("获取待处理订单列表成功，订单数: {}", orders.size());
            return Result.success(orders);
            
        } catch (Exception e) {
            LoggerUtil.error("获取待处理订单列表异常: " + e.getMessage(), e);
            return Result.error("获取待处理订单列表失败，请稍后重试");
        }
    }
}
