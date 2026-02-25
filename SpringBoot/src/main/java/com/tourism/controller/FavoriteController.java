package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Favorite;
import com.tourism.service.FavoriteService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏管理控制器
 */
@RestController
@RequestMapping("/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {
    
    private static final Logger logger = LoggerUtil.getLogger(FavoriteController.class);
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 添加收藏端点
     * @param request 添加收藏请求体
     * @return API响应
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> addFavorite(@RequestBody AddFavoriteRequest request) {
        try {
            LoggerUtil.info(logger, "处理添加收藏请求 - 用户ID: " + request.getUserId() + 
                ", 目标类型: " + request.getTargetType() + ", 目标ID: " + request.getTargetId());
            
            Favorite favorite = favoriteService.addFavorite(
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId()
            );
            
            Map<String, Object> response = buildFavoriteResponse(favorite);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "添加收藏失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 取消收藏端点
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return API响应
     */
    @DeleteMapping("/{userId}/{targetType}/{targetId}")
    public ApiResponse<String> removeFavorite(
            @PathVariable Long userId,
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        try {
            LoggerUtil.info(logger, "处理取消收藏请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType + ", 目标ID: " + targetId);
            
            favoriteService.removeFavorite(userId, targetType, targetId);
            
            return ApiResponse.success("收藏取消成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "取消收藏失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户收藏列表端点
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<Map<String, Object>> getFavoritesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取用户收藏列表请求 - 用户ID: " + userId + 
                ", 页码: " + page + ", 每页数量: " + size);
            
            Page<Favorite> favoritePage = favoriteService.getFavoritesByUser(userId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("favorites", favoritePage.getContent().stream()
                .map(this::buildFavoriteResponse)
                .collect(Collectors.toList()));
            response.put("total", favoritePage.getTotalElements());
            response.put("currentPage", favoritePage.getNumber());
            response.put("pageSize", favoritePage.getSize());
            response.put("totalPages", favoritePage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户收藏列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户指定类型的收藏列表端点
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return API响应
     */
    @GetMapping("/user/{userId}/type/{targetType}")
    public ApiResponse<Map<String, Object>> getFavoritesByUserAndType(
            @PathVariable Long userId,
            @PathVariable String targetType) {
        try {
            LoggerUtil.info(logger, "处理获取用户指定类型收藏列表请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType);
            
            List<Favorite> favorites = favoriteService.getFavoritesByUserAndType(userId, targetType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("favorites", favorites.stream()
                .map(this::buildFavoriteResponse)
                .collect(Collectors.toList()));
            response.put("total", favorites.size());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户指定类型收藏列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 检查用户是否收藏了指定目标端点
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return API响应
     */
    @GetMapping("/check/{userId}/{targetType}/{targetId}")
    public ApiResponse<Map<String, Object>> checkFavorite(
            @PathVariable Long userId,
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        try {
            LoggerUtil.info(logger, "处理检查收藏请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType + ", 目标ID: " + targetId);
            
            boolean isFavorited = favoriteService.isFavorited(userId, targetType, targetId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isFavorited", isFavorited);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "检查收藏失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 统计用户收藏数量端点
     * @param userId 用户ID
     * @return API响应
     */
    @GetMapping("/count/user/{userId}")
    public ApiResponse<Map<String, Object>> countFavoritesByUser(@PathVariable Long userId) {
        try {
            LoggerUtil.info(logger, "处理统计用户收藏数量请求 - 用户ID: " + userId);
            
            long count = favoriteService.countFavoritesByUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "统计用户收藏数量失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 统计用户指定类型的收藏数量端点
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return API响应
     */
    @GetMapping("/count/user/{userId}/type/{targetType}")
    public ApiResponse<Map<String, Object>> countFavoritesByUserAndType(
            @PathVariable Long userId,
            @PathVariable String targetType) {
        try {
            LoggerUtil.info(logger, "处理统计用户指定类型收藏数量请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType);
            
            long count = favoriteService.countFavoritesByUserAndType(userId, targetType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "统计用户指定类型收藏数量失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 统计指定目标被收藏的次数端点
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return API响应
     */
    @GetMapping("/count/target/{targetType}/{targetId}")
    public ApiResponse<Map<String, Object>> countFavoritesByTarget(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        try {
            LoggerUtil.info(logger, "处理统计目标收藏次数请求 - 目标类型: " + targetType + 
                ", 目标ID: " + targetId);
            
            long count = favoriteService.countFavoritesByTarget(targetType, targetId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "统计目标收藏次数失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户所有收藏端点
     * @param userId 用户ID
     * @return API响应
     */
    @GetMapping("/all/{userId}")
    public ApiResponse<Map<String, Object>> getAllFavoritesByUser(@PathVariable Long userId) {
        try {
            LoggerUtil.info(logger, "处理获取用户所有收藏请求 - 用户ID: " + userId);
            
            List<Favorite> favorites = favoriteService.getAllFavoritesByUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("favorites", favorites.stream()
                .map(this::buildFavoriteResponse)
                .collect(Collectors.toList()));
            response.put("total", favorites.size());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户所有收藏失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建收藏响应对象
     * @param favorite 收藏对象
     * @return 收藏响应Map
     */
    private Map<String, Object> buildFavoriteResponse(Favorite favorite) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", favorite.getId());
        response.put("userId", favorite.getUserId());
        response.put("targetType", favorite.getTargetType());
        response.put("targetId", favorite.getTargetId());
        response.put("createdAt", favorite.getCreatedAt());
        return response;
    }
    
    /**
     * 添加收藏请求体
     */
    public static class AddFavoriteRequest {
        private Long userId;
        private String targetType;
        private Long targetId;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getTargetType() {
            return targetType;
        }
        
        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }
        
        public Long getTargetId() {
            return targetId;
        }
        
        public void setTargetId(Long targetId) {
            this.targetId = targetId;
        }
    }
}
