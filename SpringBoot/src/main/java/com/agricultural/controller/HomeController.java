package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.AgriculturalProduct;
import com.agricultural.entity.Warning;
import com.agricultural.repository.AgriculturalProductRepository;
import com.agricultural.repository.OrderRepository;
import com.agricultural.repository.UserRepository;
import com.agricultural.repository.WarningRepository;
import com.agricultural.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页数据看板控制层
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarningRepository warningRepository;

    @Autowired
    private AgriculturalProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        LoggerUtil.info("收到获取首页看板真实数据请求");
        try {
            Map<String, Object> data = new HashMap<>();

            // 1. 顶部统计概览数据
            Map<String, Long> stats = new HashMap<>();
            stats.put("totalUsers", userRepository.count());
            stats.put("totalProducts", productRepository.count());
            stats.put("totalOrders", orderRepository.count());

            // 活跃预警数 (假设预警枚举状态名为 ACTIVE)
            List<Warning> allWarnings = warningRepository.findAll();
            long activeWarnings = allWarnings.stream()
                    .filter(w -> w.getStatus() != null && "ACTIVE".equalsIgnoreCase(w.getStatus().name()))
                    .count();
            stats.put("activeWarnings", activeWarnings);

            data.put("stats", stats);

            // 2. 最新预警信息 (倒序排列，取最新发布的 5 条)
            List<Warning> recentWarnings = allWarnings.stream()
                    .sorted((w1, w2) -> w2.getCreatedAt().compareTo(w1.getCreatedAt()))
                    .limit(5)
                    .collect(Collectors.toList());
            data.put("recentWarnings", recentWarnings);

            // 3. 热门农资产品 (这里以库存降序模拟热门商品，取前 5 条)
            List<AgriculturalProduct> topProducts = productRepository.findAll().stream()
                    .sorted((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()))
                    .limit(5)
                    .collect(Collectors.toList());
            data.put("topProducts", topProducts);

            return Result.success("获取首页数据成功", data);

        } catch (Exception e) {
            LoggerUtil.error("获取首页看板数据异常: " + e.getMessage(), e);
            return Result.error("获取首页数据失败");
        }
    }
}