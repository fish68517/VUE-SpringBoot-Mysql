package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.BrowsingHistory;
import com.tourism.service.BrowsingHistoryService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 浏览历史控制器
 */
@RestController
@RequestMapping("/api/browsing-history")
@CrossOrigin(origins = "*")
public class BrowsingHistoryController {
    
    private static final Logger logger = LoggerUtil.getLogger(BrowsingHistoryController.class);
    
    @Autowired
    private BrowsingHistoryService browsingHistoryService;
    
    /**
     * 记录浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return API响应
     */
    @PostMapping("/record")
    public ApiResponse<BrowsingHistory> recordBrowsingHistory(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        try {
            LoggerUtil.info(logger, "记录浏览历史请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType + ", 目标ID: " + targetId);
            
            BrowsingHistory history = browsingHistoryService.recordBrowsingHistory(userId, targetType, targetId);
            return ApiResponse.success(history);
        } catch (Exception e) {
            LoggerUtil.error(logger, "记录浏览历史失败: " + e.getMessage());
            return ApiResponse.error("记录浏览历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户浏览历史（分页）
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<Map<String, Object>> getBrowsingHistoryByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "查询用户浏览历史请求 - 用户ID: " + userId + 
                ", 页码: " + page + ", 每页数量: " + size);
            
            Page<BrowsingHistory> historyPage = browsingHistoryService.getBrowsingHistoryByUser(userId, page, size);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", historyPage.getContent());
            result.put("totalElements", historyPage.getTotalElements());
            result.put("totalPages", historyPage.getTotalPages());
            result.put("currentPage", historyPage.getNumber());
            result.put("pageSize", historyPage.getSize());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            LoggerUtil.error(logger, "查询浏览历史失败: " + e.getMessage());
            return ApiResponse.error("查询浏览历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户指定类型的浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return API响应
     */
    @GetMapping("/user/{userId}/type/{targetType}")
    public ApiResponse<List<BrowsingHistory>> getBrowsingHistoryByUserAndType(
            @PathVariable Long userId,
            @PathVariable String targetType) {
        try {
            LoggerUtil.info(logger, "查询用户浏览历史请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType);
            
            List<BrowsingHistory> history = browsingHistoryService.getBrowsingHistoryByUserAndType(userId, targetType);
            return ApiResponse.success(history);
        } catch (Exception e) {
            LoggerUtil.error(logger, "查询浏览历史失败: " + e.getMessage());
            return ApiResponse.error("查询浏览历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户所有浏览历史
     * @param userId 用户ID
     * @return API响应
     */
    @GetMapping("/user/{userId}/all")
    public ApiResponse<List<BrowsingHistory>> getAllBrowsingHistoryByUser(
            @PathVariable Long userId) {
        try {
            LoggerUtil.info(logger, "查询用户所有浏览历史请求 - 用户ID: " + userId);
            
            List<BrowsingHistory> history = browsingHistoryService.getAllBrowsingHistoryByUser(userId);
            return ApiResponse.success(history);
        } catch (Exception e) {
            LoggerUtil.error(logger, "查询浏览历史失败: " + e.getMessage());
            return ApiResponse.error("查询浏览历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计用户浏览历史数量
     * @param userId 用户ID
     * @return API响应
     */
    @GetMapping("/user/{userId}/count")
    public ApiResponse<Long> countBrowsingHistoryByUser(
            @PathVariable Long userId) {
        try {
            LoggerUtil.info(logger, "统计用户浏览历史数量请求 - 用户ID: " + userId);
            
            long count = browsingHistoryService.countBrowsingHistoryByUser(userId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            LoggerUtil.error(logger, "统计浏览历史数量失败: " + e.getMessage());
            return ApiResponse.error("统计浏览历史数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计用户指定类型的浏览历史数量
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return API响应
     */
    @GetMapping("/user/{userId}/type/{targetType}/count")
    public ApiResponse<Long> countBrowsingHistoryByUserAndType(
            @PathVariable Long userId,
            @PathVariable String targetType) {
        try {
            LoggerUtil.info(logger, "统计用户浏览历史数量请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType);
            
            long count = browsingHistoryService.countBrowsingHistoryByUserAndType(userId, targetType);
            return ApiResponse.success(count);
        } catch (Exception e) {
            LoggerUtil.error(logger, "统计浏览历史数量失败: " + e.getMessage());
            return ApiResponse.error("统计浏览历史数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户指定类型的浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return API响应
     */
    @DeleteMapping("/user/{userId}/type/{targetType}")
    public ApiResponse<String> deleteBrowsingHistoryByUserAndType(
            @PathVariable Long userId,
            @PathVariable String targetType) {
        try {
            LoggerUtil.info(logger, "删除用户浏览历史请求 - 用户ID: " + userId + 
                ", 目标类型: " + targetType);
            
            browsingHistoryService.deleteBrowsingHistoryByUserAndType(userId, targetType);
            return ApiResponse.success("浏览历史已删除");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除浏览历史失败: " + e.getMessage());
            return ApiResponse.error("删除浏览历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户所有浏览历史
     * @param userId 用户ID
     * @return API响应
     */
    @DeleteMapping("/user/{userId}/all")
    public ApiResponse<String> deleteAllBrowsingHistoryByUser(
            @PathVariable Long userId) {
        try {
            LoggerUtil.info(logger, "删除用户所有浏览历史请求 - 用户ID: " + userId);
            
            browsingHistoryService.deleteAllBrowsingHistoryByUser(userId);
            return ApiResponse.success("所有浏览历史已删除");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除浏览历史失败: " + e.getMessage());
            return ApiResponse.error("删除浏览历史失败: " + e.getMessage());
        }
    }
}
