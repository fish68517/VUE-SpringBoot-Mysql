package com.animal.controller;

import com.animal.mapper.CartMapper;
import com.animal.mapper.OrderDetailMapper;
import com.animal.mapper.OrderMapper;
import com.animal.mapper.RecipeMapper;
import com.animal.mapper.UserMapper;
import com.animal.model.Order;
import com.animal.model.OrderDetail;
import com.animal.model.OrderItemRequest;
import com.animal.model.OrderRequest;
import com.animal.model.Recipe;
import com.animal.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_MERCHANT = "merchant";

    private static final String STATUS_PENDING = "\u5f85\u4ed8\u6b3e";
    private static final String STATUS_PAID = "\u5df2\u4ed8\u6b3e";
    private static final String STATUS_FINISHED = "\u5df2\u5b8c\u6210";
    private static final String STATUS_TAKEN = "\u5df2\u53d6\u8d70";
    private static final String STATUS_CANCELED = "\u5df2\u53d6\u6d88";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/user")
    public ResponseEntity<?> getUserOrders(
            @RequestParam("userId") Integer userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer limit) {

        if (limit != null && limit > 0) {
            List<Order> limited = orderMapper.findByUserByPage(userId, 0, limit, query);
            attachOrderDetails(limited);
            return ResponseEntity.ok(limited);
        }

        if (page != null && pageSize != null) {
            int safePage = Math.max(page, 1);
            int safePageSize = Math.max(pageSize, 1);
            int offset = (safePage - 1) * safePageSize;

            int total = orderMapper.countByUser(userId, query);
            List<Order> list = orderMapper.findByUserByPage(userId, offset, safePageSize, query);
            attachOrderDetails(list);
            return ResponseEntity.ok(buildPageResult(total, list));
        }

        List<Order> orders = orderMapper.findByUserId(userId);
        attachOrderDetails(orders);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAdminOrders(
            @RequestParam("userId") Integer userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer limit) {
        User operator = userMapper.findById(userId);
        if (operator == null || !ROLE_ADMIN.equals(operator.getRole())) {
            return ResponseEntity.status(403).body("No permission");
        }

        if (limit != null && limit > 0) {
            List<Order> limited = orderMapper.findAllByPage(0, limit, query);
            attachOrderDetails(limited);
            return ResponseEntity.ok(limited);
        }

        if (page != null && pageSize != null) {
            int safePage = Math.max(page, 1);
            int safePageSize = Math.max(pageSize, 1);
            int offset = (safePage - 1) * safePageSize;

            int total = orderMapper.countAll(query);
            List<Order> list = orderMapper.findAllByPage(offset, safePageSize, query);
            attachOrderDetails(list);
            return ResponseEntity.ok(buildPageResult(total, list));
        }

        List<Order> orders = orderMapper.findAllOrders();
        attachOrderDetails(orders);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/merchant")
    public ResponseEntity<?> getMerchantOrders(
            @RequestParam("userId") Integer userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer limit) {
        User operator = userMapper.findById(userId);
        if (operator == null || !ROLE_MERCHANT.equals(operator.getRole())) {
            return ResponseEntity.status(403).body("No permission");
        }

        if (limit != null && limit > 0) {
            List<Order> limited = orderMapper.findByMerchantByPage(userId, 0, limit, query);
            attachOrderDetailsForMerchant(limited, userId);
            return ResponseEntity.ok(limited);
        }

        if (page != null && pageSize != null) {
            int safePage = Math.max(page, 1);
            int safePageSize = Math.max(pageSize, 1);
            int offset = (safePage - 1) * safePageSize;

            int total = orderMapper.countByMerchant(userId, query);
            List<Order> list = orderMapper.findByMerchantByPage(userId, offset, safePageSize, query);
            attachOrderDetailsForMerchant(list, userId);
            return ResponseEntity.ok(buildPageResult(total, list));
        }

        List<Order> orders = orderMapper.findByMerchantId(userId);
        attachOrderDetailsForMerchant(orders, userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Integer id) {
        Order order = orderMapper.findById(id);
        if (order != null) {
            order.setOrderDetails(orderDetailMapper.findByOrderId(order.getId()));
        }
        return order;
    }

    @GetMapping("/waiting-count")
    public Map<Integer, Integer> getWaitingCount() {
        List<Map<String, Object>> rows = orderMapper.countWaitingByWindow();
        Map<Integer, Integer> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Integer windowId = ((Number) row.get("windowId")).intValue();
            Integer waitingCount = ((Number) row.get("waitingCount")).intValue();
            result.put(windowId, waitingCount);
        }
        return result;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createOrder(
            @RequestParam("userId") Integer userId,
            @RequestBody OrderRequest request) {

        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Order items cannot be empty");
        }

        List<OrderDetail> detailsToInsert = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest item : request.getItems()) {
            if (item.getRecipeId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Invalid order item");
            }

            Recipe recipe = recipeMapper.findById(item.getRecipeId());
            if (recipe == null) {
                return ResponseEntity.badRequest().body("Recipe not found");
            }
            if (recipe.getPrice() == null) {
                return ResponseEntity.badRequest().body("Recipe price is missing");
            }

            BigDecimal linePrice = recipe.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(linePrice);

            OrderDetail detail = new OrderDetail();
            detail.setRecipeId(item.getRecipeId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(recipe.getPrice());
            detailsToInsert.add(detail);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setPickupCode(generatePickupCode());
        order.setRemark(request.getRemark());
        order.setStatus(STATUS_PENDING);
        order.setCreatedAt(new Date());
        order.setTotalAmount(totalAmount);
        orderMapper.insert(order);

        for (OrderDetail detail : detailsToInsert) {
            detail.setOrderId(order.getId());
            orderDetailMapper.insert(detail);
        }

        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            cartMapper.deleteByUserIdAndIds(userId, request.getCartItemIds());
        } else {
            cartMapper.deleteByUserId(userId);
        }

        order.setOrderDetails(orderDetailMapper.findByOrderId(order.getId()));
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @RequestParam("userId") Integer userId,
            @PathVariable Integer id,
            @RequestBody Order order) {
        User operator = userMapper.findById(userId);
        if (operator == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Order currentOrder = orderMapper.findById(id);
        if (currentOrder == null) {
            return ResponseEntity.notFound().build();
        }

        String targetStatus = order == null ? null : order.getStatus();
        if (!isValidStatus(targetStatus)) {
            return ResponseEntity.badRequest().body("Invalid order status");
        }

        String currentStatus = currentOrder.getStatus();
        if (!isTransitionAllowed(currentStatus, targetStatus)) {
            return ResponseEntity.badRequest().body("Invalid status transition");
        }

        String role = operator.getRole();
        if (ROLE_ADMIN.equals(role)) {
            // Admin follows the state machine only.
        } else if (ROLE_MERCHANT.equals(role)) {
            if (!belongsToMerchant(id, userId)) {
                return ResponseEntity.status(403).body("No permission");
            }
            boolean merchantCanFinish = STATUS_PAID.equals(currentStatus) && STATUS_FINISHED.equals(targetStatus);
            if (!merchantCanFinish) {
                return ResponseEntity.status(403).body("Merchant can only mark paid order as finished");
            }
        } else {
            if (!userId.equals(currentOrder.getUserId())) {
                return ResponseEntity.status(403).body("No permission");
            }
            boolean canPayOrCancel = STATUS_PENDING.equals(currentStatus)
                    && (STATUS_PAID.equals(targetStatus) || STATUS_CANCELED.equals(targetStatus));
            boolean canTake = STATUS_FINISHED.equals(currentStatus) && STATUS_TAKEN.equals(targetStatus);
            if (!(canPayOrCancel || canTake)) {
                return ResponseEntity.status(403).body("Operation is not allowed in current status");
            }
        }

        Order update = new Order();
        update.setId(id);
        update.setStatus(targetStatus);
        orderMapper.updateStatus(update);

        return ResponseEntity.ok(orderMapper.findById(id));
    }

    private Map<String, Object> buildPageResult(int total, List<Order> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", data);
        return result;
    }

    private boolean belongsToMerchant(Integer orderId, Integer merchantId) {
        List<OrderDetail> details = orderDetailMapper.findByOrderId(orderId);
        return details.stream().anyMatch(d -> d.getRecipe() != null && merchantId.equals(d.getRecipe().getMerchantId()));
    }

    private boolean isValidStatus(String status) {
        return STATUS_PENDING.equals(status)
                || STATUS_PAID.equals(status)
                || STATUS_FINISHED.equals(status)
                || STATUS_TAKEN.equals(status)
                || STATUS_CANCELED.equals(status);
    }

    private boolean isTransitionAllowed(String from, String to) {
        if (from == null || to == null) {
            return false;
        }
        if (from.equals(to)) {
            return true;
        }
        if (STATUS_PENDING.equals(from)) {
            return STATUS_PAID.equals(to) || STATUS_CANCELED.equals(to);
        }
        if (STATUS_PAID.equals(from)) {
            return STATUS_FINISHED.equals(to);
        }
        if (STATUS_FINISHED.equals(from)) {
            return STATUS_TAKEN.equals(to);
        }
        return false;
    }

    private void attachOrderDetails(List<Order> orders) {
        for (Order order : orders) {
            order.setOrderDetails(orderDetailMapper.findByOrderId(order.getId()));
        }
    }

    private void attachOrderDetailsForMerchant(List<Order> orders, Integer merchantId) {
        for (Order order : orders) {
            List<OrderDetail> details = orderDetailMapper.findByOrderId(order.getId());
            List<OrderDetail> mine = new ArrayList<>();
            for (OrderDetail detail : details) {
                if (detail.getRecipe() != null && merchantId.equals(detail.getRecipe().getMerchantId())) {
                    mine.add(detail);
                }
            }
            order.setOrderDetails(mine);
        }
    }

    private String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis();
    }

    private String generatePickupCode() {
        int code = 100000 + new Random().nextInt(900000);
        return String.valueOf(code);
    }
}
