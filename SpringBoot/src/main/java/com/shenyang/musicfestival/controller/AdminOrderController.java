package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.entity.Order;
import com.shenyang.musicfestival.entity.OrderItem;
import com.shenyang.musicfestival.entity.User;
import com.shenyang.musicfestival.repository.OrderItemRepository;
import com.shenyang.musicfestival.repository.OrderRepository;
import com.shenyang.musicfestival.repository.UserRepository;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    // ==========================================
    // 1. 获取商品订单列表 (带分页、状态过滤、搜索)
    // 解决报错：/api/admin/orders?page=0&size=10&status=paid
    // ==========================================
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        // 取出所有 order_type 为 'product' 的商品订单，按时间倒序
        List<Order> productOrders = orderRepository.findAll().stream()
                .filter(o -> "product".equals(o.getOrderType()))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());

        List<Map<String, Object>> dtoList = new ArrayList<>();

        for (Order order : productOrders) {
            // 1. 状态过滤
            if (status != null && !status.trim().isEmpty() && !status.equals(order.getStatus())) {
                continue;
            }

            // 2. 获取买家手机号
            User user = userRepository.findById(order.getUserId()).orElse(null);
            String phone = user != null ? user.getPhone() : "未知用户";

            // 3. 关键词过滤 (支持搜订单号或手机号)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String orderIdStr = String.valueOf(order.getId());
                if (!phone.contains(keyword) && !orderIdStr.contains(keyword)) {
                    continue;
                }
            }

            Long orderId =order.getId();
            List<OrderItem> orderItem =orderItemRepository.findByOrderId(orderId);
            // 4. 组装前端需要的安全数据
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", order.getId());
            dto.put("orderNo", "PRD" + 100000 + order.getId()); // 生成逼真的订单编号展示
            dto.put("userId", order.getUserId());
            dto.put("buyerPhone", phone);
            dto.put("totalAmount", order.getTotalAmount());
            dto.put("status", order.getStatus());
            dto.put("shippingAddress", order.getShippingAddress());
            dto.put("trackingNumber", order.getTrackingNumber());
            dto.put("createdAt", order.getCreatedAt());
            dto.put("items", orderItem); // 附带订单内的商品明细

            dtoList.add(dto);
        }

        // 5. 在内存中手动切割分页，绝对防错
        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        List<Map<String, Object>> pagedList = dtoList.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> resultPage = new PageImpl<>(pagedList, pageable, dtoList.size());

        return ResponseEntity.ok(ApiResponse.success(resultPage, "获取商品订单列表成功"));
    }

    // ==========================================
    // 2. 订单发货 (填写物流单号)
    // 防止你点击页面的"发货"按钮报错
    // ==========================================
    @PostMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Void>> shipOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        String trackingNumber = body.get("trackingNumber");

        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("物流单号不能为空");
        }

        order.setTrackingNumber(trackingNumber);
        order.setStatus("shipped"); // 将状态改为"已发货"
        orderRepository.save(order);

        return ResponseEntity.ok(ApiResponse.success(null, "订单发货成功"));
    }

    // ==========================================
    // 3. 更新订单状态 (用于取消订单 / 标记已完成 等快捷操作)
    // ==========================================
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        String newStatus = body.get("status");

        if (newStatus != null && !newStatus.trim().isEmpty()) {
            order.setStatus(newStatus);
            orderRepository.save(order);
        }

        return ResponseEntity.ok(ApiResponse.success(null, "订单状态更新成功"));
    }


    @PutMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Void>> updateOrderShip(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        String newStatus = body.get("status");

        if (newStatus != null && !newStatus.trim().isEmpty()) {
            order.setStatus(newStatus);
            orderRepository.save(order);
        } else {
            order.setStatus("shipped");
            orderRepository.save(order);
        }

        return ResponseEntity.ok(ApiResponse.success(null, "订单状态更新成功"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> orderComplete(
            @PathVariable Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        order.setStatus("completed");
        orderRepository.save(order);

        return ResponseEntity.ok(ApiResponse.success(null, "订单状态更新成功"));
    }


}