package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.Recommendation;
import com.agricultural.service.RecommendationService;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制层
 * 
 * 处理农资推荐相关的HTTP请求，包括生成推荐、获取推荐列表、接受/拒绝推荐等操作
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 生成推荐接口
     * 
     * 根据预警信息为用户生成农资推荐
     * 
     * @param warningId 预警ID（必填）
     * @param userId 用户ID（必填）
     * @return 生成的推荐列表
     */
    @PostMapping
    public Result<List<Recommendation>> generateRecommendations(
            @RequestParam @NotNull(message = "预警ID不能为空") Long warningId,
            @RequestParam @NotNull(message = "用户ID不能为空") Long userId) {
        
        LoggerUtil.info("收到生成推荐请求，预警ID: {}, 用户ID: {}", warningId, userId);
        
        try {
            // 参数验证
            if (warningId == null || warningId <= 0) {
                LoggerUtil.warn("生成推荐请求参数验证失败: 预警ID无效");
                return Result.validationError("预警ID无效");
            }
            
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("生成推荐请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层生成推荐
            List<Recommendation> recommendations = recommendationService.generateRecommendations(warningId, userId);
            
            LoggerUtil.info("生成推荐成功，预警ID: {}, 用户ID: {}, 推荐数: {}", warningId, userId, recommendations.size());
            return Result.success("推荐生成成功", recommendations);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("生成推荐失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("生成推荐异常: " + e.getMessage(), e);
            return Result.error("生成推荐失败，请稍后重试");
        }
    }

    /**
     * 获取推荐列表接口
     * 
     * 获取所有推荐信息，支持按预警ID、用户ID、产品ID、推荐状态等条件筛选
     * 
     * @param warningId 预警ID（可选）
     * @param userId 用户ID（可选）
     * @param productId 产品ID（可选）
     * @param status 推荐状态（可选，PENDING/ACCEPTED/REJECTED）
     * @return 推荐列表
     */
    @GetMapping
    public Result<List<Recommendation>> getRecommendationList(
            @RequestParam(required = false) Long warningId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Recommendation.RecommendationStatus status) {
        
        LoggerUtil.info("收到获取推荐列表请求，预警ID: {}, 用户ID: {}, 产品ID: {}, 状态: {}", 
                warningId, userId, productId, status);
        
        try {
            List<Recommendation> recommendations;
            
            // 根据条件查询推荐
            if (warningId != null && warningId > 0 && userId != null && userId > 0) {
                // 按预警ID和用户ID查询
                recommendations = recommendationService.getRecommendationsByWarningAndUser(warningId, userId);
            } else if (warningId != null && warningId > 0) {
                // 按预警ID查询
                recommendations = recommendationService.getRecommendationsByWarningId(warningId);
            } else if (userId != null && userId > 0) {
                // 按用户ID查询
                recommendations = recommendationService.getRecommendationsByUserId(userId);
            } else if (productId != null && productId > 0) {
                // 按产品ID查询
                recommendations = recommendationService.getRecommendationsByProductId(productId);
            } else if (status != null) {
                // 按状态查询
                recommendations = recommendationService.getRecommendationsByStatus(status);
            } else {
                // 获取所有推荐
                recommendations = recommendationService.getRecommendationList();
            }
            
            LoggerUtil.info("获取推荐列表成功，推荐总数: {}", recommendations.size());
            return Result.success(recommendations);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取推荐列表失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取推荐列表异常: " + e.getMessage(), e);
            return Result.error("获取推荐列表失败，请稍后重试");
        }
    }

    /**
     * 获取推荐详情接口
     * 
     * 根据推荐ID获取推荐详细信息
     * 
     * @param id 推荐ID（必填）
     * @return 推荐详情
     */
    @GetMapping("/{id}")
    public Result<Recommendation> getRecommendationById(
            @PathVariable @NotNull(message = "推荐ID不能为空") Long id) {
        
        LoggerUtil.info("收到获取推荐详情请求，推荐ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("获取推荐详情请求参数验证失败: 推荐ID无效");
                return Result.validationError("推荐ID无效");
            }
            
            // 调用业务层获取推荐详情
            Recommendation recommendation = recommendationService.getRecommendationById(id);
            
            LoggerUtil.info("获取推荐详情成功，推荐ID: {}", id);
            return Result.success(recommendation);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取推荐详情失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取推荐详情异常: " + e.getMessage(), e);
            return Result.error("获取推荐详情失败，请稍后重试");
        }
    }

    /**
     * 获取推荐列表接口（按预警ID）
     * 
     * 获取指定预警的所有推荐信息，按优先级排序
     * 
     * @param warningId 预警ID（必填）
     * @return 推荐列表
     */
    @GetMapping("/warning/{warningId}")
    public Result<List<Recommendation>> getRecommendationsByWarningId(
            @PathVariable @NotNull(message = "预警ID不能为空") Long warningId) {
        
        LoggerUtil.info("收到按预警ID获取推荐列表请求，预警ID: {}", warningId);
        
        try {
            // 参数验证
            if (warningId == null || warningId <= 0) {
                LoggerUtil.warn("按预警ID获取推荐列表请求参数验证失败: 预警ID无效");
                return Result.validationError("预警ID无效");
            }
            
            // 调用业务层按预警ID获取推荐列表
            List<Recommendation> recommendations = recommendationService.getRecommendationsByWarningId(warningId);
            
            LoggerUtil.info("按预警ID获取推荐列表成功，预警ID: {}, 推荐数: {}", warningId, recommendations.size());
            return Result.success(recommendations);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按预警ID获取推荐列表失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按预警ID获取推荐列表异常: " + e.getMessage(), e);
            return Result.error("按预警ID获取推荐列表失败，请稍后重试");
        }
    }

    /**
     * 获取推荐列表接口（按用户ID）
     * 
     * 获取指定用户的所有推荐信息
     * 
     * @param userId 用户ID（必填）
     * @return 推荐列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Recommendation>> getRecommendationsByUserId(
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        
        LoggerUtil.info("收到按用户ID获取推荐列表请求，用户ID: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("按用户ID获取推荐列表请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层按用户ID获取推荐列表
            List<Recommendation> recommendations = recommendationService.getRecommendationsByUserId(userId);
            
            LoggerUtil.info("按用户ID获取推荐列表成功，用户ID: {}, 推荐数: {}", userId, recommendations.size());
            return Result.success(recommendations);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按用户ID获取推荐列表失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按用户ID获取推荐列表异常: " + e.getMessage(), e);
            return Result.error("按用户ID获取推荐列表失败，请稍后重试");
        }
    }

    /**
     * 接受推荐接口
     * 
     * 用户接受推荐，将推荐状态更新为已接受
     * 
     * @param id 推荐ID（必填）
     * @return 更新后的推荐信息
     */
    @PostMapping("/{id}/accept")
    public Result<Recommendation> acceptRecommendation(
            @PathVariable @NotNull(message = "推荐ID不能为空") Long id) {
        
        LoggerUtil.info("收到接受推荐请求，推荐ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("接受推荐请求参数验证失败: 推荐ID无效");
                return Result.validationError("推荐ID无效");
            }
            
            // 调用业务层接受推荐
            Recommendation acceptedRecommendation = recommendationService.acceptRecommendation(id);
            
            LoggerUtil.info("接受推荐成功，推荐ID: {}", id);
            return Result.success("推荐已接受", acceptedRecommendation);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("接受推荐失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("接受推荐异常: " + e.getMessage(), e);
            return Result.error("接受推荐失败，请稍后重试");
        }
    }

    /**
     * 拒绝推荐接口
     * 
     * 用户拒绝推荐，将推荐状态更新为已拒绝
     * 
     * @param id 推荐ID（必填）
     * @return 更新后的推荐信息
     */
    @PostMapping("/{id}/reject")
    public Result<Recommendation> rejectRecommendation(
            @PathVariable @NotNull(message = "推荐ID不能为空") Long id) {
        
        LoggerUtil.info("收到拒绝推荐请求，推荐ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("拒绝推荐请求参数验证失败: 推荐ID无效");
                return Result.validationError("推荐ID无效");
            }
            
            // 调用业务层拒绝推荐
            Recommendation rejectedRecommendation = recommendationService.rejectRecommendation(id);
            
            LoggerUtil.info("拒绝推荐成功，推荐ID: {}", id);
            return Result.success("推荐已拒绝", rejectedRecommendation);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("拒绝推荐失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("拒绝推荐异常: " + e.getMessage(), e);
            return Result.error("拒绝推荐失败，请稍后重试");
        }
    }

    /**
     * 删除推荐接口
     * 
     * 从数据库中删除指定的推荐记录
     * 
     * @param id 推荐ID（必填）
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRecommendation(
            @PathVariable @NotNull(message = "推荐ID不能为空") Long id) {
        
        LoggerUtil.info("收到删除推荐请求，推荐ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("删除推荐请求参数验证失败: 推荐ID无效");
                return Result.validationError("推荐ID无效");
            }
            
            // 调用业务层删除推荐
            recommendationService.deleteRecommendation(id);
            
            LoggerUtil.info("删除推荐成功，推荐ID: {}", id);
            return Result.success();
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("删除推荐失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("删除推荐异常: " + e.getMessage(), e);
            return Result.error("删除推荐失败，请稍后重试");
        }
    }

    /**
     * 获取待处理推荐列表接口
     * 
     * 获取所有状态为待处理的推荐信息，按优先级排序
     * 
     * @return 待处理推荐列表
     */
    @GetMapping("/pending")
    public Result<List<Recommendation>> getPendingRecommendations() {
        
        LoggerUtil.info("收到获取待处理推荐列表请求");
        
        try {
            // 调用业务层获取待处理推荐
            List<Recommendation> pendingRecommendations = recommendationService.getPendingRecommendations();
            
            LoggerUtil.info("获取待处理推荐列表成功，待处理推荐数: {}", pendingRecommendations.size());
            return Result.success(pendingRecommendations);
            
        } catch (Exception e) {
            LoggerUtil.error("获取待处理推荐列表异常: " + e.getMessage(), e);
            return Result.error("获取待处理推荐列表失败，请稍后重试");
        }
    }

    /**
     * 获取推荐统计信息接口
     * 
     * 获取推荐统计信息（包括不同状态的推荐数量）
     * 
     * @return 推荐统计信息
     */
    @GetMapping("/statistics")
    public Result<String> getRecommendationStatistics() {
        
        LoggerUtil.info("收到获取推荐统计信息请求");
        
        try {
            // 调用业务层获取推荐统计信息
            String statistics = recommendationService.getRecommendationStatistics();
            
            LoggerUtil.info("获取推荐统计信息成功");
            return Result.success(statistics);
            
        } catch (Exception e) {
            LoggerUtil.error("获取推荐统计信息异常: " + e.getMessage(), e);
            return Result.error("获取推荐统计信息失败，请稍后重试");
        }
    }

    /**
     * 获取推荐总数接口
     * 
     * 获取系统中所有推荐的总数
     * 
     * @return 推荐总数
     */
    @GetMapping("/count")
    public Result<Long> getRecommendationCount() {
        
        LoggerUtil.info("收到获取推荐总数请求");
        
        try {
            // 调用业务层获取推荐总数
            long count = recommendationService.getRecommendationCount();
            
            LoggerUtil.info("获取推荐总数成功，推荐总数: {}", count);
            return Result.success(count);
            
        } catch (Exception e) {
            LoggerUtil.error("获取推荐总数异常: " + e.getMessage(), e);
            return Result.error("获取推荐总数失败，请稍后重试");
        }
    }

    /**
     * 获取用户推荐总数接口
     * 
     * 获取指定用户的推荐数量
     * 
     * @param userId 用户ID（必填）
     * @return 用户推荐总数
     */
    @GetMapping("/count/user/{userId}")
    public Result<Long> getRecommendationCountByUser(
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        
        LoggerUtil.info("收到获取用户推荐总数请求，用户ID: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("获取用户推荐总数请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层获取用户推荐总数
            long count = recommendationService.getRecommendationCountByUser(userId);
            
            LoggerUtil.info("获取用户推荐总数成功，用户ID: {}, 推荐总数: {}", userId, count);
            return Result.success(count);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取用户推荐总数失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取用户推荐总数异常: " + e.getMessage(), e);
            return Result.error("获取用户推荐总数失败，请稍后重试");
        }
    }

    /**
     * 检查推荐是否存在接口
     * 
     * 检查指定推荐是否存在
     * 
     * @param id 推荐ID（必填）
     * @return 推荐是否存在
     */
    @GetMapping("/exists/{id}")
    public Result<Boolean> recommendationExists(
            @PathVariable @NotNull(message = "推荐ID不能为空") Long id) {
        
        LoggerUtil.info("收到检查推荐是否存在请求，推荐ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("检查推荐是否存在请求参数验证失败: 推荐ID无效");
                return Result.validationError("推荐ID无效");
            }
            
            // 调用业务层检查推荐是否存在
            boolean exists = recommendationService.recommendationExists(id);
            
            LoggerUtil.info("检查推荐是否存在完成，推荐ID: {}, 是否存在: {}", id, exists);
            return Result.success(exists);
            
        } catch (Exception e) {
            LoggerUtil.error("检查推荐是否存在异常: " + e.getMessage(), e);
            return Result.error("检查推荐是否存在失败，请稍后重试");
        }
    }
}
