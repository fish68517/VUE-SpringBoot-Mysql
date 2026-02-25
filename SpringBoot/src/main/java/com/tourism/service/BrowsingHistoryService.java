package com.tourism.service;

import com.tourism.entity.BrowsingHistory;
import com.tourism.exception.BusinessException;
import com.tourism.repository.BrowsingHistoryRepository;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 浏览历史业务逻辑服务
 */
@Service
public class BrowsingHistoryService {
    
    private static final Logger logger = LoggerUtil.getLogger(BrowsingHistoryService.class);
    
    @Autowired
    private BrowsingHistoryRepository browsingHistoryRepository;
    
    /**
     * 记录浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型（attraction、hotel 或 product）
     * @param targetId 目标ID
     * @return 创建的浏览历史对象
     */
    public BrowsingHistory recordBrowsingHistory(Long userId, String targetType, Long targetId) {
        // 验证输入参数
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "记录浏览历史失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "记录浏览历史失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        if (!("attraction".equals(targetType) || "hotel".equals(targetType) || "product".equals(targetType))) {
            LoggerUtil.warn(logger, "记录浏览历史失败：目标类型无效 - " + targetType);
            throw new BusinessException("目标类型无效，只支持 attraction、hotel 或 product");
        }
        
        if (targetId == null || targetId <= 0) {
            LoggerUtil.warn(logger, "记录浏览历史失败：目标ID无效 - " + targetId);
            throw new BusinessException("目标ID无效");
        }
        
        // 创建浏览历史
        BrowsingHistory history = new BrowsingHistory();
        history.setUserId(userId);
        history.setTargetType(targetType);
        history.setTargetId(targetId);
        
        // 保存浏览历史
        BrowsingHistory savedHistory = browsingHistoryRepository.save(history);
        LoggerUtil.info(logger, "记录浏览历史成功 - 用户ID: " + userId + 
            ", 目标类型: " + targetType + ", 目标ID: " + targetId);
        
        return savedHistory;
    }
    
    /**
     * 根据用户ID查询浏览历史（分页，按创建时间倒序）
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 浏览历史分页数据
     */
    public Page<BrowsingHistory> getBrowsingHistoryByUser(Long userId, int page, int size) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "查询浏览历史失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BrowsingHistory> history = browsingHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        LoggerUtil.info(logger, "查询浏览历史成功 - 用户ID: " + userId);
        return history;
    }
    
    /**
     * 根据用户ID和目标类型查询浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 浏览历史列表
     */
    public List<BrowsingHistory> getBrowsingHistoryByUserAndType(Long userId, String targetType) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "查询浏览历史失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "查询浏览历史失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        List<BrowsingHistory> history = browsingHistoryRepository.findByUserIdAndTargetType(userId, targetType);
        LoggerUtil.info(logger, "查询浏览历史成功 - 用户ID: " + userId + ", 目标类型: " + targetType);
        return history;
    }
    
    /**
     * 根据用户ID查询所有浏览历史
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    public List<BrowsingHistory> getAllBrowsingHistoryByUser(Long userId) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "查询所有浏览历史失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        List<BrowsingHistory> history = browsingHistoryRepository.findByUserId(userId);
        LoggerUtil.info(logger, "查询所有浏览历史成功 - 用户ID: " + userId);
        return history;
    }
    
    /**
     * 统计用户的浏览历史数量
     * @param userId 用户ID
     * @return 浏览历史数量
     */
    public long countBrowsingHistoryByUser(Long userId) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "统计浏览历史数量失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        long count = browsingHistoryRepository.countByUserId(userId);
        LoggerUtil.info(logger, "统计浏览历史数量成功 - 用户ID: " + userId + ", 数量: " + count);
        return count;
    }
    
    /**
     * 统计用户指定类型的浏览历史数量
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 浏览历史数量
     */
    public long countBrowsingHistoryByUserAndType(Long userId, String targetType) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "统计浏览历史数量失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "统计浏览历史数量失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        long count = browsingHistoryRepository.countByUserIdAndTargetType(userId, targetType);
        LoggerUtil.info(logger, "统计浏览历史数量成功 - 用户ID: " + userId + 
            ", 目标类型: " + targetType + ", 数量: " + count);
        return count;
    }
    
    /**
     * 删除用户指定类型的浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型
     */
    public void deleteBrowsingHistoryByUserAndType(Long userId, String targetType) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "删除浏览历史失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "删除浏览历史失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        browsingHistoryRepository.deleteByUserIdAndTargetType(userId, targetType);
        LoggerUtil.info(logger, "删除浏览历史成功 - 用户ID: " + userId + ", 目标类型: " + targetType);
    }
    
    /**
     * 删除用户所有浏览历史
     * @param userId 用户ID
     */
    public void deleteAllBrowsingHistoryByUser(Long userId) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "删除所有浏览历史失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        browsingHistoryRepository.deleteByUserId(userId);
        LoggerUtil.info(logger, "删除所有浏览历史成功 - 用户ID: " + userId);
    }
}
