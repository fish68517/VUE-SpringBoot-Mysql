package com.animal.controller;

import com.animal.mapper.CartMapper;
import com.animal.mapper.OrderDetailMapper;
import com.animal.mapper.OrderMapper;
import com.animal.mapper.RecipeMapper;
import com.animal.model.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @GetMapping("/user")
    public List<Order> getUserOrders(@RequestParam("userId") Integer userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        // 获取订单详情
        for (Order order : orders) {
            List<OrderDetail> details = orderDetailMapper.findByOrderId(order.getId());
            order.setOrderDetails(details);
        }
        return orders;
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Integer id) {
        Order order = orderMapper.findById(id);
        if (order != null) {
            List<OrderDetail> details = orderDetailMapper.findByOrderId(order.getId());
            order.setOrderDetails(details);
        }
        return order;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createOrder(
            @RequestParam("userId") Integer userId,
            @RequestBody OrderRequest request) {
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setStatus("待付款");
        order.setCreatedAt(new Date());

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest item : request.getItems()) {
            Recipe recipe = recipeMapper.findById(item.getRecipeId());
            if (recipe == null) {
                throw new RuntimeException("商品不存在");
            }
            /*BigDecimal itemAmount = BigDecimal.valueOf(recipe.getPrice()).multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);*/
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            // 随机生成一个金额
            totalAmount = new BigDecimal(Math.random() * 100);
        }
        order.setTotalAmount(totalAmount);

        // 保存订单
        orderMapper.insert(order);

        // 保存订单详情
        for (OrderItemRequest item : request.getItems()) {
            Recipe recipe = recipeMapper.findById(item.getRecipeId());
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setRecipeId(item.getRecipeId());
            detail.setQuantity(item.getQuantity());
            //detail.setPrice(BigDecimal.valueOf(recipe.getPrice()));
            orderDetailMapper.insert(detail);
        }

        // 清空购物车
        cartMapper.deleteByUserId(userId);

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Integer id, @RequestBody Order order) {
        order.setId(id);
        orderMapper.updateStatus(order);
        return order;
    }

    // 生成订单号
    private String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis();
    }
}

// 请求对象
