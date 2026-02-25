package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Order;
import com.tourism.entity.OrderItem;
import com.tourism.service.OrderService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单管理控制器
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {
    
    private static final Logger logger = LoggerUtil.getLogger(OrderController.class);
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建景点门票订单端点
     * @param request 创建订单请求体
     * @return API响应
     */
    @PostMapping("/attractions/create")
    public ApiResponse<Map<String, Object>> createAttractionOrder(@RequestBody CreateAttractionOrderRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建景点订单请求 - 用户ID: " + request.getUserId() + ", 景点ID: " + request.getAttractionId());
            
            Order order = orderService.createAttractionOrder(
                request.getUserId(),
                request.getAttractionId(),
                request.getQuantity(),
                request.getVisitDate()
            );
            
            Map<String, Object> response = buildOrderResponse(order);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建景点订单失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 创建商品购买订单端点
     * @param request 创建订单请求体
     * @return API响应
     */
    @PostMapping("/products/create")
    public ApiResponse<Map<String, Object>> createProductOrder(@RequestBody CreateProductOrderRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建商品订单请求 - 用户ID: " + request.getUserId() + ", 商品ID: " + request.getProductId());
            
            Order order = orderService.createProductOrder(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity()
            );
            
            Map<String, Object> response = buildOrderResponse(order);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建商品订单失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 创建酒店预订订单端点
     * @param request 创建订单请求体
     * @return API响应
     */
    @PostMapping("/hotels/create")
    public ApiResponse<Map<String, Object>> createHotelOrder(@RequestBody CreateHotelOrderRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建酒店订单请求 - 用户ID: " + request.getUserId() + ", 酒店ID: " + request.getHotelId());
            
            Order order = orderService.createHotelOrder(
                request.getUserId(),
                request.getHotelId(),
                request.getRoomType(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getQuantity()
            );
            
            Map<String, Object> response = buildOrderResponse(order);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建酒店订单失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户订单列表端点（分页）
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取用户订单列表请求 - 用户ID: " + userId + ", 页码: " + page);
            
            Page<Order> orderPage = orderService.getUserOrders(userId, page, size);
            
            List<Map<String, Object>> orders = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("total", orderPage.getTotalElements());
            response.put("currentPage", orderPage.getNumber());
            response.put("pageSize", orderPage.getSize());
            response.put("totalPages", orderPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户订单列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户指定状态的订单列表端点（分页）
     * @param userId 用户ID
     * @param status 订单状态
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ApiResponse<Map<String, Object>> getUserOrdersByStatus(
            @PathVariable Long userId,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取用户指定状态订单列表请求 - 用户ID: " + userId + ", 状态: " + status);
            
            Page<Order> orderPage = orderService.getUserOrdersByStatus(userId, status, page, size);
            
            List<Map<String, Object>> orders = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("total", orderPage.getTotalElements());
            response.put("currentPage", orderPage.getNumber());
            response.put("pageSize", orderPage.getSize());
            response.put("totalPages", orderPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户指定状态订单列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单详情端点
     * @param orderId 订单ID
     * @return API响应
     */
    @GetMapping("/{orderId}")
    public ApiResponse<Map<String, Object>> getOrderDetail(@PathVariable Long orderId) {
        try {
            LoggerUtil.info(logger, "处理获取订单详情请求 - 订单ID: " + orderId);
            
            Order order = orderService.getOrderDetail(orderId);
            List<OrderItem> items = orderService.getOrderItems(orderId);
            
            Map<String, Object> response = buildOrderDetailResponse(order, items);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取订单详情失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 取消订单端点
     * @param orderId 订单ID
     * @return API响应
     */
    @PutMapping("/{orderId}/cancel")
    public ApiResponse<Map<String, Object>> cancelOrder(@PathVariable Long orderId) {
        try {
            LoggerUtil.info(logger, "处理取消订单请求 - 订单ID: " + orderId);
            
            Order order = orderService.cancelOrder(orderId);
            Map<String, Object> response = buildOrderResponse(order);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "取消订单失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新订单状态端点（管理员）
     * @param orderId 订单ID
     * @param request 更新请求体
     * @return API响应
     */
    @PutMapping("/admin/{orderId}/status")
    public ApiResponse<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新订单状态请求 - 订单ID: " + orderId + ", 新状态: " + request.getStatus());
            
            Order order = orderService.updateOrderStatus(orderId, request.getStatus());
            Map<String, Object> response = buildOrderResponse(order);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新订单状态失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有订单列表端点（分页，管理员）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/admin/all")
    public ApiResponse<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取所有订单列表请求 - 页码: " + page);
            
            Page<Order> orderPage = orderService.getAllOrders(page, size);
            
            List<Map<String, Object>> orders = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("total", orderPage.getTotalElements());
            response.put("currentPage", orderPage.getNumber());
            response.put("pageSize", orderPage.getSize());
            response.put("totalPages", orderPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取所有订单列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取指定状态的订单列表端点（分页，管理员）
     * @param status 订单状态
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/admin/status/{status}")
    public ApiResponse<Map<String, Object>> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取指定状态订单列表请求 - 状态: " + status + ", 页码: " + page);
            
            Page<Order> orderPage = orderService.getOrdersByStatus(status, page, size);
            
            List<Map<String, Object>> orders = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("total", orderPage.getTotalElements());
            response.put("currentPage", orderPage.getNumber());
            response.put("pageSize", orderPage.getSize());
            response.put("totalPages", orderPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取指定状态订单列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取指定类型的订单列表端点（分页，管理员）
     * @param orderType 订单类型
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/admin/type/{orderType}")
    public ApiResponse<Map<String, Object>> getOrdersByType(
            @PathVariable String orderType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取指定类型订单列表请求 - 类型: " + orderType + ", 页码: " + page);
            
            Page<Order> orderPage = orderService.getOrdersByType(orderType, page, size);
            
            List<Map<String, Object>> orders = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("total", orderPage.getTotalElements());
            response.put("currentPage", orderPage.getNumber());
            response.put("pageSize", orderPage.getSize());
            response.put("totalPages", orderPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取指定类型订单列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单统计信息端点（管理员）
     * @return API响应
     */
    @GetMapping("/admin/statistics")
    public ApiResponse<Map<String, Object>> getOrderStatistics() {
        try {
            LoggerUtil.info(logger, "处理获取订单统计信息请求");
            
            Map<String, Object> statistics = orderService.getOrderStatistics();
            
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取订单统计信息失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建订单响应对象
     * @param order 订单对象
     * @return 响应Map
     */
    private Map<String, Object> buildOrderResponse(Order order) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.getId());
        response.put("orderNumber", order.getOrderNumber());
        response.put("userId", order.getUserId());
        response.put("totalPrice", order.getTotalPrice());
        response.put("status", order.getStatus());
        response.put("orderType", order.getOrderType());
        response.put("createdAt", order.getCreatedAt());
        response.put("updatedAt", order.getUpdatedAt());
        return response;
    }
    
    /**
     * 构建订单详情响应对象
     * @param order 订单对象
     * @param items 订单项目列表
     * @return 响应Map
     */
    private Map<String, Object> buildOrderDetailResponse(Order order, List<OrderItem> items) {
        Map<String, Object> response = buildOrderResponse(order);
        
        List<Map<String, Object>> itemList = items.stream()
            .map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("itemType", item.getItemType());
                itemMap.put("itemId", item.getItemId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("unitPrice", item.getUnitPrice());
                itemMap.put("subtotal", item.getSubtotal());
                itemMap.put("createdAt", item.getCreatedAt());
                return itemMap;
            })
            .collect(Collectors.toList());
        
        response.put("items", itemList);
        return response;
    }
    
    /**
     * 创建景点订单请求体
     */
    public static class CreateAttractionOrderRequest {
        private Long userId;
        private Long attractionId;
        private Integer quantity;
        private String visitDate;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getAttractionId() { return attractionId; }
        public void setAttractionId(Long attractionId) { this.attractionId = attractionId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public String getVisitDate() { return visitDate; }
        public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
    }
    
    /**
     * 创建酒店订单请求体
     */
    public static class CreateHotelOrderRequest {
        private Long userId;
        private Long hotelId;
        private String roomType;
        private String checkInDate;
        private String checkOutDate;
        private Integer quantity;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getHotelId() { return hotelId; }
        public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
        
        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }
        
        public String getCheckInDate() { return checkInDate; }
        public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
        
        public String getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    /**
     * 更新订单状态请求体
     */
    public static class UpdateOrderStatusRequest {
        private String status;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    /**
     * 创建商品订单请求体
     */
    public static class CreateProductOrderRequest {
        private Long userId;
        private Long productId;
        private Integer quantity;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
