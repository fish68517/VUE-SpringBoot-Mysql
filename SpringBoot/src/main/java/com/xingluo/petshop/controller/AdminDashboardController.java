package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员数据看板控制器
 * 提供平台数据统计和分析功能
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final StatisticsService statisticsService;
    
    /**
     * 获取数据看板
     * GET /api/admin/dashboard
     * 返回平台的基本统计数据：用户总数、店铺总数、商品总数、订单总数
     * @return 数据看板信息
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 统计用户总数
        Long totalUsers = statisticsService.countTotalUsers();
        dashboard.put("totalUsers", totalUsers);
        
        // 统计店铺总数
        Long totalShops = statisticsService.countTotalShops();
        dashboard.put("totalShops", totalShops);
        
        // 统计商品总数
        Long totalProducts = statisticsService.countTotalProducts();
        dashboard.put("totalProducts", totalProducts);
        
        // 统计订单总数
        Long totalOrders = statisticsService.countTotalOrders();
        dashboard.put("totalOrders", totalOrders);
        
        return ApiResponse.ok(dashboard);
    }
    
    /**
     * 获取时间范围统计
     * GET /api/admin/dashboard/statistics
     * 返回指定时间范围内的新增用户数、新增订单数和交易金额
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     * @return 时间范围统计数据
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        
        // 解析时间参数
        LocalDateTime start = LocalDateTime.parse(startTime.replace(" ", "T"));
        LocalDateTime end = LocalDateTime.parse(endTime.replace(" ", "T"));
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计新增用户数
        Long newUsers = statisticsService.countNewUsers(start, end);
        statistics.put("newUsers", newUsers);
        
        // 统计新增订单数
        Long newOrders = statisticsService.countNewOrders(start, end);
        statistics.put("newOrders", newOrders);
        
        // 统计交易金额
        BigDecimal totalAmount = statisticsService.sumOrderAmount(start, end);
        statistics.put("totalAmount", totalAmount);
        
        statistics.put("startTime", startTime);
        statistics.put("endTime", endTime);
        
        return ApiResponse.ok(statistics);
    }
    
    /**
     * 获取分类统计
     * GET /api/admin/dashboard/category
     * 返回各分类的商品数量和销售额
     * @return 分类统计数据
     */
    @GetMapping("/category")
    public ApiResponse<Map<String, Map<String, Object>>> getCategoryStatistics() {
        Map<String, Map<String, Object>> categoryStats = statisticsService.getCategoryStatistics();
        return ApiResponse.ok(categoryStats);
    }
}
