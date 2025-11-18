package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.BrowseHistory;
import com.xingluo.petshop.repository.BrowseHistoryRepository;
import com.xingluo.petshop.service.BrowseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 浏览历史服务实现类
 */
@Service
@RequiredArgsConstructor
public class BrowseHistoryServiceImpl implements BrowseHistoryService {
    
    private final BrowseHistoryRepository browseHistoryRepository;
    
    @Override
    @Transactional
    public void recordBrowseHistory(Long userId, Long productId) {
        // 检查是否已有该商品的浏览记录
        BrowseHistory existingHistory = browseHistoryRepository
                .findFirstByUserIdAndProductIdOrderByCreateTimeDesc(userId, productId);
        
        // 如果已存在，更新时间；否则创建新记录
        if (existingHistory != null) {
            // 删除旧记录，创建新记录以更新时间
            browseHistoryRepository.delete(existingHistory);
        }
        
        // 创建新的浏览历史记录
        BrowseHistory browseHistory = new BrowseHistory();
        browseHistory.setUserId(userId);
        browseHistory.setProductId(productId);
        browseHistory.setCreateTime(LocalDateTime.now());
        
        browseHistoryRepository.save(browseHistory);
    }
    
    @Override
    public List<BrowseHistory> getUserBrowseHistory(Long userId) {
        return browseHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    @Override
    public List<Long> getRecentBrowsedProductIds(Long userId, int limit) {
        List<BrowseHistory> histories = browseHistoryRepository.findRecentByUserId(userId);
        
        return histories.stream()
                .limit(limit)
                .map(BrowseHistory::getProductId)
                .distinct()
                .collect(Collectors.toList());
    }
}
