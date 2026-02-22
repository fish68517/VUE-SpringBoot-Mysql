package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.Order;
import com.agricultural.entity.User;
import com.agricultural.entity.Warning;
import com.agricultural.entity.AgriculturalProduct;
import com.agricultural.repository.OrderRepository;
import com.agricultural.repository.WarningRepository;
import com.agricultural.repository.UserRepository;
import com.agricultural.repository.AgriculturalProductRepository;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析控制层
 * 
 * 处理数据分析和报表相关的HTTP请求，包括订单统计、预警统计、用户统计、产品销售排行等
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WarningRepository warningRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgriculturalProductRepository productRepository;

    /**
     * 获取订单统计接口
     * 
     * 统计订单数据，包括总订单数、各状态订单数、总销售额等
     * 
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @return 订单统计数据
     */
    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrderStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LoggerUtil.info("收到获取订单统计请求，开始日期: {}, 结束日期: {}", startDate, endDate);
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取所有订单或指定时间范围内的订单
            List<Order> orders;
            if (startDate != null && endDate != null) {
                LocalDateTime start = DateUtil.parseDateTime(startDate + " 00:00:00");
                LocalDateTime end = DateUtil.parseDateTime(endDate + " 23:59:59");
                orders = orderRepository.findByTimeRange(start, end);
            } else {
                orders = orderRepository.findAll();
            }
            
            // 计算总订单数
            long totalOrders = orders.size();
            statistics.put("totalOrders", totalOrders);
            
            // 计算各状态订单数
            Map<String, Long> statusCount = orders.stream()
                    .collect(Collectors.groupingBy(
                            o -> o.getStatus().toString(),
                            Collectors.counting()
                    ));
            statistics.put("ordersByStatus", statusCount);
            
            // 计算总销售额
            BigDecimal totalSales = orders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.put("totalSales", totalSales);
            
            // 计算平均订单金额
            BigDecimal averageOrderAmount = totalOrders > 0 
                    ? totalSales.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO;
            statistics.put("averageOrderAmount", averageOrderAmount);
            
            // 计算已支付订单数和已支付金额
            List<Order> paidOrders = orders.stream()
                    .filter(o -> o.getStatus() == Order.OrderStatus.PAID || 
                               o.getStatus() == Order.OrderStatus.SHIPPED ||
                               o.getStatus() == Order.OrderStatus.DELIVERED)
                    .collect(Collectors.toList());
            
            BigDecimal paidAmount = paidOrders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            statistics.put("paidOrders", (long) paidOrders.size());
            statistics.put("paidAmount", paidAmount);
            
            LoggerUtil.info("获取订单统计成功，总订单数: {}, 总销售额: {}", totalOrders, totalSales);
            return Result.success("订单统计获取成功", statistics);
            
        } catch (Exception e) {
            LoggerUtil.error("获取订单统计异常: " + e.getMessage(), e);
            return Result.error("获取订单统计失败，请稍后重试");
        }
    }

    /**
     * 获取预警统计接口
     * 
     * 统计预警数据，包括总预警数、各类型预警数、各等级预警数等
     * 
     * @param region 地区（可选）
     * @return 预警统计数据
     */
    @GetMapping("/warnings")
    public Result<Map<String, Object>> getWarningStatistics(
            @RequestParam(required = false) String region) {
        
        LoggerUtil.info("收到获取预警统计请求，地区: {}", region);
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取所有预警或指定地区的预警
            List<Warning> warnings;
            if (region != null && !region.isEmpty()) {
                warnings = warningRepository.findByRegion(region);
            } else {
                warnings = warningRepository.findAll();
            }
            
            // 计算总预警数
            long totalWarnings = warnings.size();
            statistics.put("totalWarnings", totalWarnings);
            
            // 计算各类型预警数
            Map<String, Long> typeCount = warnings.stream()
                    .collect(Collectors.groupingBy(
                            Warning::getWarningType,
                            Collectors.counting()
                    ));
            statistics.put("warningsByType", typeCount);
            
            // 计算各等级预警数
            Map<String, Long> severityCount = warnings.stream()
                    .collect(Collectors.groupingBy(
                            w -> w.getSeverity().toString(),
                            Collectors.counting()
                    ));
            statistics.put("warningsBySeverity", severityCount);
            
            // 计算各状态预警数
            Map<String, Long> statusCount = warnings.stream()
                    .collect(Collectors.groupingBy(
                            w -> w.getStatus().toString(),
                            Collectors.counting()
                    ));
            statistics.put("warningsByStatus", statusCount);
            
            // 计算活跃预警数
            long activeWarnings = warnings.stream()
                    .filter(w -> w.getStatus() == Warning.WarningStatus.ACTIVE)
                    .count();
            statistics.put("activeWarnings", activeWarnings);
            
            LoggerUtil.info("获取预警统计成功，总预警数: {}, 活跃预警数: {}", totalWarnings, activeWarnings);
            return Result.success("预警统计获取成功", statistics);
            
        } catch (Exception e) {
            LoggerUtil.error("获取预警统计异常: " + e.getMessage(), e);
            return Result.error("获取预警统计失败，请稍后重试");
        }
    }

    /**
     * 获取用户统计接口
     * 
     * 统计用户数据，包括总用户数、各类型用户数、各地区用户数等
     * 
     * @return 用户统计数据
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserStatistics() {
        
        LoggerUtil.info("收到获取用户统计请求");
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取所有用户
            List<User> users = userRepository.findAll();
            
            // 计算总用户数
            long totalUsers = users.size();
            statistics.put("totalUsers", totalUsers);
            
            // 计算各类型用户数
            Map<String, Long> typeCount = users.stream()
                    .collect(Collectors.groupingBy(
                            u -> u.getUserType().toString(),
                            Collectors.counting()
                    ));
            statistics.put("usersByType", typeCount);
            
            // 计算各地区用户数
            Map<String, Long> regionCount = users.stream()
                    .filter(u -> u.getRegion() != null && !u.getRegion().isEmpty())
                    .collect(Collectors.groupingBy(
                            User::getRegion,
                            Collectors.counting()
                    ));
            statistics.put("usersByRegion", regionCount);
            
            // 计算各状态用户数
            Map<String, Long> statusCount = users.stream()
                    .collect(Collectors.groupingBy(
                            u -> u.getStatus().toString(),
                            Collectors.counting()
                    ));
            statistics.put("usersByStatus", statusCount);
            
            // 计算活跃用户数
            long activeUsers = users.stream()
                    .filter(u -> u.getStatus() == User.UserStatus.ACTIVE)
                    .count();
            statistics.put("activeUsers", activeUsers);
            
            LoggerUtil.info("获取用户统计成功，总用户数: {}, 活跃用户数: {}", totalUsers, activeUsers);
            return Result.success("用户统计获取成功", statistics);
            
        } catch (Exception e) {
            LoggerUtil.error("获取用户统计异常: " + e.getMessage(), e);
            return Result.error("获取用户统计失败，请稍后重试");
        }
    }

    /**
     * 获取产品销售排行接口
     * 
     * 统计产品销售数据，返回销售排行前N的产品
     * 
     * @param limit 返回的产品数量（默认10）
     * @return 产品销售排行数据
     */
    @GetMapping("/products/top")
    public Result<List<Map<String, Object>>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        LoggerUtil.info("收到获取产品销售排行请求，限制数量: {}", limit);
        
        try {
            // 参数验证
            if (limit == null || limit <= 0) {
                LoggerUtil.warn("获取产品销售排行请求参数验证失败: 限制数量无效");
                return Result.validationError("限制数量必须大于0");
            }
            
            if (limit > 100) {
                limit = 100; // 最多返回100条
            }
            
            // 获取所有订单
            List<Order> orders = orderRepository.findAll();
            
            // 按产品ID分组统计销售数量和销售额
            Map<Long, Map<String, Object>> productStats = new HashMap<>();
            
            for (Order order : orders) {
                if (order.getProductId() != null) {
                    Long productId = order.getProductId();
                    
                    if (!productStats.containsKey(productId)) {
                        Map<String, Object> stats = new HashMap<>();
                        stats.put("productId", productId);
                        stats.put("quantity", 0);
                        stats.put("totalSales", BigDecimal.ZERO);
                        productStats.put(productId, stats);
                    }
                    
                    Map<String, Object> stats = productStats.get(productId);
                    stats.put("quantity", (Integer) stats.get("quantity") + order.getQuantity());
                    stats.put("totalSales", ((BigDecimal) stats.get("totalSales")).add(order.getTotalPrice()));
                }
            }
            
            // 获取产品信息并补充到统计数据中
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<Long, Map<String, Object>> entry : productStats.entrySet()) {
                Optional<AgriculturalProduct> product = productRepository.findById(entry.getKey());
                if (product.isPresent()) {
                    Map<String, Object> item = entry.getValue();
                    item.put("productName", product.get().getProductName());
                    item.put("category", product.get().getCategory());
                    item.put("price", product.get().getPrice());
                    result.add(item);
                }
            }
            
            // 按销售额排序并限制数量
            List<Map<String, Object>> topProducts = result.stream()
                    .sorted((a, b) -> ((BigDecimal) b.get("totalSales"))
                            .compareTo((BigDecimal) a.get("totalSales")))
                    .limit(limit)
                    .collect(Collectors.toList());
            
            LoggerUtil.info("获取产品销售排行成功，返回产品数: {}", topProducts.size());
            return Result.success("产品销售排行获取成功", topProducts);
            
        } catch (Exception e) {
            LoggerUtil.error("获取产品销售排行异常: " + e.getMessage(), e);
            return Result.error("获取产品销售排行失败，请稍后重试");
        }
    }

    /**
     * 数据导出接口
     * 
     * 导出各类统计数据，支持多种格式
     * 
     * @param dataType 数据类型（orders、warnings、users、products）
     * @param format 导出格式（json、csv）
     * @return 导出的数据
     */
    @GetMapping("/export")
    public Result<Map<String, Object>> exportData(
            @RequestParam(defaultValue = "orders") String dataType,
            @RequestParam(defaultValue = "json") String format) {
        
        LoggerUtil.info("收到数据导出请求，数据类型: {}, 格式: {}", dataType, format);
        
        try {
            // 参数验证
            if (!dataType.matches("orders|warnings|users|products")) {
                LoggerUtil.warn("数据导出请求参数验证失败: 无效的数据类型 {}", dataType);
                return Result.validationError("无效的数据类型，支持: orders, warnings, users, products");
            }
            
            if (!format.matches("json|csv")) {
                LoggerUtil.warn("数据导出请求参数验证失败: 无效的导出格式 {}", format);
                return Result.validationError("无效的导出格式，支持: json, csv");
            }
            
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("dataType", dataType);
            exportData.put("format", format);
            exportData.put("exportTime", LocalDateTime.now());
            
            // 根据数据类型导出相应数据
            switch (dataType) {
                case "orders":
                    List<Order> orders = orderRepository.findAll();
                    exportData.put("data", orders);
                    exportData.put("count", orders.size());
                    break;
                    
                case "warnings":
                    List<Warning> warnings = warningRepository.findAll();
                    exportData.put("data", warnings);
                    exportData.put("count", warnings.size());
                    break;
                    
                case "users":
                    List<User> users = userRepository.findAll();
                    exportData.put("data", users);
                    exportData.put("count", users.size());
                    break;
                    
                case "products":
                    List<AgriculturalProduct> products = productRepository.findAll();
                    exportData.put("data", products);
                    exportData.put("count", products.size());
                    break;
            }
            
            LoggerUtil.info("数据导出成功，数据类型: {}, 导出数量: {}", dataType, exportData.get("count"));
            return Result.success("数据导出成功", exportData);
            
        } catch (Exception e) {
            LoggerUtil.error("数据导出异常: " + e.getMessage(), e);
            return Result.error("数据导出失败，请稍后重试");
        }
    }
}
