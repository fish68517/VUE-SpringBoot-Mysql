package com.xingluo.petshop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 统计用户总数
     * @return 用户总数
     */
    Long countTotalUsers();
    
    /**
     * 统计店铺总数
     * @return 店铺总数
     */
    Long countTotalShops();
    
    /**
     * 统计商品总数
     * @return 商品总数
     */
    Long countTotalProducts();
    
    /**
     * 统计订单总数
     * @return 订单总数
     */
    Long countTotalOrders();
    
    /**
     * 按时间范围统计新增用户数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 新增用户数
     */
    Long countNewUsers(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 按时间范围统计新增订单数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 新增订单数
     */
    Long countNewOrders(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 按时间范围统计交易金额
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易金额
     */
    BigDecimal sumOrderAmount(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计各分类商品数量和销售额
     * @return Map<分类名称, Map<"productCount"|"salesAmount", 值>>
     */
    Map<String, Map<String, Object>> getCategoryStatistics();
}
