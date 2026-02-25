package com.tourism.service;

import com.tourism.entity.Favorite;
import com.tourism.exception.BusinessException;
import com.tourism.repository.FavoriteRepository;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 收藏业务逻辑服务
 */
@Service
public class FavoriteService {
    
    private static final Logger logger = LoggerUtil.getLogger(FavoriteService.class);
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    /**
     * 添加收藏
     * @param userId 用户ID
     * @param targetType 目标类型（attraction、hotel 或 product）
     * @param targetId 目标ID
     * @return 创建的收藏对象
     */
    public Favorite addFavorite(Long userId, String targetType, Long targetId) {
        // 验证输入参数
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "添加收藏失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "添加收藏失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        if (!("attraction".equals(targetType) || "hotel".equals(targetType) || "product".equals(targetType))) {
            LoggerUtil.warn(logger, "添加收藏失败：目标类型无效 - " + targetType);
            throw new BusinessException("目标类型无效，只支持 attraction、hotel 或 product");
        }
        
        if (targetId == null || targetId <= 0) {
            LoggerUtil.warn(logger, "添加收藏失败：目标ID无效 - " + targetId);
            throw new BusinessException("目标ID无效");
        }
        
        // 检查是否已收藏
        if (favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            LoggerUtil.warn(logger, "添加收藏失败：已收藏 - 用户ID: " + userId + 
                ", 目标类型: " + targetType + ", 目标ID: " + targetId);
            throw new BusinessException("已收藏该项目");
        }
        
        // 创建收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        
        // 保存收藏
        Favorite savedFavorite = favoriteRepository.save(favorite);
        LoggerUtil.info(logger, "添加收藏成功 - 用户ID: " + userId + 
            ", 目标类型: " + targetType + ", 目标ID: " + targetId);
        
        return savedFavorite;
    }
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     */
    public void removeFavorite(Long userId, String targetType, Long targetId) {
        // 验证输入参数
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "取消收藏失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "取消收藏失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        if (targetId == null || targetId <= 0) {
            LoggerUtil.warn(logger, "取消收藏失败：目标ID无效 - " + targetId);
            throw new BusinessException("目标ID无效");
        }
        
        // 查找收藏
        Optional<Favorite> favoriteOptional = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(
            userId, targetType, targetId
        );
        
        if (!favoriteOptional.isPresent()) {
            LoggerUtil.warn(logger, "取消收藏失败：收藏不存在 - 用户ID: " + userId + 
                ", 目标类型: " + targetType + ", 目标ID: " + targetId);
            throw new BusinessException("收藏不存在");
        }
        
        // 删除收藏
        favoriteRepository.deleteById(favoriteOptional.get().getId());
        LoggerUtil.info(logger, "取消收藏成功 - 用户ID: " + userId + 
            ", 目标类型: " + targetType + ", 目标ID: " + targetId);
    }
    
    /**
     * 根据ID查询收藏
     * @param id 收藏ID
     * @return 收藏对象
     */
    public Optional<Favorite> findById(Long id) {
        return favoriteRepository.findById(id);
    }
    
    /**
     * 根据用户ID查询收藏列表（分页）
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 收藏分页数据
     */
    public Page<Favorite> getFavoritesByUser(Long userId, int page, int size) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "查询收藏列表失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteRepository.findByUserId(userId, pageable);
        LoggerUtil.info(logger, "查询收藏列表成功 - 用户ID: " + userId);
        return favorites;
    }
    
    /**
     * 根据用户ID和目标类型查询收藏列表
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 收藏列表
     */
    public List<Favorite> getFavoritesByUserAndType(Long userId, String targetType) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "查询收藏列表失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "查询收藏列表失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        List<Favorite> favorites = favoriteRepository.findByUserIdAndTargetType(userId, targetType);
        LoggerUtil.info(logger, "查询收藏列表成功 - 用户ID: " + userId + ", 目标类型: " + targetType);
        return favorites;
    }
    
    /**
     * 检查用户是否收藏了指定目标
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 是否收藏
     */
    public boolean isFavorited(Long userId, String targetType, Long targetId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        
        if (targetType == null || targetType.isEmpty() || targetId == null || targetId <= 0) {
            return false;
        }
        
        return favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }
    
    /**
     * 统计用户的收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    public long countFavoritesByUser(Long userId) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "统计收藏数量失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        long count = favoriteRepository.countByUserId(userId);
        LoggerUtil.info(logger, "统计收藏数量成功 - 用户ID: " + userId + ", 数量: " + count);
        return count;
    }
    
    /**
     * 统计用户指定类型的收藏数量
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 收藏数量
     */
    public long countFavoritesByUserAndType(Long userId, String targetType) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "统计收藏数量失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "统计收藏数量失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        long count = favoriteRepository.countByUserIdAndTargetType(userId, targetType);
        LoggerUtil.info(logger, "统计收藏数量成功 - 用户ID: " + userId + 
            ", 目标类型: " + targetType + ", 数量: " + count);
        return count;
    }
    
    /**
     * 统计指定目标被收藏的次数
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 收藏次数
     */
    public long countFavoritesByTarget(String targetType, Long targetId) {
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "统计收藏次数失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        if (targetId == null || targetId <= 0) {
            LoggerUtil.warn(logger, "统计收藏次数失败：目标ID无效 - " + targetId);
            throw new BusinessException("目标ID无效");
        }
        
        long count = favoriteRepository.countByTargetTypeAndTargetId(targetType, targetId);
        LoggerUtil.info(logger, "统计收藏次数成功 - 目标类型: " + targetType + 
            ", 目标ID: " + targetId + ", 次数: " + count);
        return count;
    }
    
    /**
     * 根据用户ID查询所有收藏
     * @param userId 用户ID
     * @return 收藏列表
     */
    public List<Favorite> getAllFavoritesByUser(Long userId) {
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "查询所有收藏失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        LoggerUtil.info(logger, "查询所有收藏成功 - 用户ID: " + userId);
        return favorites;
    }
}
