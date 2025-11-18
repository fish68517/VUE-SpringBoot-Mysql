package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.BrowseHistory;

import java.util.List;

/**
 * 浏览历史服务接口
 */
public interface BrowseHistoryService {
    
    /**
     * 记录浏览历史
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void recordBrowseHistory(Long userId, Long productId);
    
    /**
     * 获取用户浏览历史
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    List<BrowseHistory> getUserBrowseHistory(Long userId);
    
    /**
     * 获取用户最近浏览的商品ID列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 商品ID列表
     */
    List<Long> getRecentBrowsedProductIds(Long userId, int limit);
}
