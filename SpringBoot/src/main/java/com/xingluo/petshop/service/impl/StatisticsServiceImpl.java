package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.Category;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.repository.*;
import com.xingluo.petshop.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    
    @Override
    public Long countTotalUsers() {
        return userRepository.count();
    }
    
    @Override
    public Long countTotalShops() {
        return shopRepository.count();
    }
    
    @Override
    public Long countTotalProducts() {
        return productRepository.count();
    }
    
    @Override
    public Long countTotalOrders() {
        return orderRepository.count();
    }
    
    @Override
    public Long countNewUsers(LocalDateTime startTime, LocalDateTime endTime) {
        return userRepository.countByCreateTimeBetween(startTime, endTime);
    }
    
    @Override
    public Long countNewOrders(LocalDateTime startTime, LocalDateTime endTime) {
        return orderRepository.countByCreateTimeBetween(startTime, endTime);
    }
    
    @Override
    public BigDecimal sumOrderAmount(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal amount = orderRepository.sumAmountByCreateTimeBetween(startTime, endTime);
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    @Override
    public Map<String, Map<String, Object>> getCategoryStatistics() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        // 获取所有分类
        List<Category> categories = categoryRepository.findAll();
        
        // 对每个分类统计商品数量和销售额
        for (Category category : categories) {
            Map<String, Object> stats = new HashMap<>();
            
            // 统计该分类下的商品数量
            List<Product> products = productRepository.findAll().stream()
                    .filter(p -> p.getCategoryId().equals(category.getId()))
                    .collect(Collectors.toList());
            
            stats.put("productCount", (long) products.size());
            
            // 统计该分类下的销售额（按销量计算）
            BigDecimal salesAmount = products.stream()
                    .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getSales() != null ? p.getSales() : 0)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            stats.put("salesAmount", salesAmount);
            
            result.put(category.getName(), stats);
        }
        
        return result;
    }
}
