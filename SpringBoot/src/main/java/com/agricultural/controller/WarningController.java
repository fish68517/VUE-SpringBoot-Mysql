package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.dto.WarningUpdateRequest;
import com.agricultural.entity.Warning;
import com.agricultural.service.WarningService;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警控制层
 * 
 * 处理极端天气预警相关的HTTP请求，包括发布预警、获取预警列表、更新预警、删除预警等操作
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/warnings")
public class WarningController {

    @Autowired
    private WarningService warningService;

    /**
     * 发布预警接口
     * 
     * 创建新的极端天气预警信息

     */
    @PostMapping
    public Result<Warning> publishWarning(@RequestBody @jakarta.validation.Valid com.agricultural.dto.WarningRequest request) {

        LoggerUtil.info("收到发布预警请求，预警类型: {}, 地区: {}, 等级: {}",
                request.getWarningType(), request.getRegion(), request.getSeverity());

        try {
            // 时间逻辑验证
      /*      if (request.getStartTime().isAfter(request.getEndTime())) {
                LoggerUtil.warn("发布预警请求参数验证失败: 开始时间晚于结束时间");
                return Result.validationError("开始时间不能晚于结束时间");
            }*/

            // 调用业务层发布预警
            Warning publishedWarning = warningService.publishWarning(
                    request.getWarningType(),
                    request.getRegion(),
                    request.getSeverity(),
                    request.getDescription(),
                    request.getStartTime(),
                    request.getEndTime()
            );

            LoggerUtil.info("发布预警成功，预警ID: {}", publishedWarning.getId());
            return Result.success("预警发布成功", publishedWarning);

        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("发布预警失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("发布预警异常: " + e.getMessage(), e);
            return Result.error("发布预警失败，请稍后重试");
        }
    }

    /**
     * 获取预警列表接口
     * 
     * 获取所有预警信息，支持按地区、预警类型、预警状态等条件筛选
     * 
     * @param region 地区（可选）
     * @param warningType 预警类型（可选）
     * @param status 预警状态（可选，ACTIVE/EXPIRED/CANCELLED）
     * @return 预警列表
     */
    @GetMapping
    public Result<List<Warning>> getWarningList(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String warningType,
            @RequestParam(required = false) Warning.WarningSeverity severity, // 👈 新增这个参数
            @RequestParam(required = false) Warning.WarningStatus status) {

        LoggerUtil.info("收到获取预警列表请求，地区: {}, 预警类型: {}, 等级: {}, 状态: {}",
                region, warningType, severity, status);

        try {
            // 直接把所有参数传给 Service 层统一处理，不再使用繁琐的 if-else
            List<Warning> warnings = warningService.getWarningsByCondition(region, warningType, severity, status);

            LoggerUtil.info("获取预警列表成功，预警总数: {}", warnings.size());
            return Result.success(warnings);

        } catch (Exception e) {
            LoggerUtil.error("获取预警列表异常: " + e.getMessage(), e);
            return Result.error("获取预警列表失败，请稍后重试");
        }
    }

    /**
     * 获取预警详情接口
     * 
     * 根据预警ID获取预警详细信息
     * 
     * @param id 预警ID（必填）
     * @return 预警详情
     */
    @GetMapping("/{id}")
    public Result<Warning> getWarningById(
            @PathVariable @NotNull(message = "预警ID不能为空") Long id) {
        
        LoggerUtil.info("收到获取预警详情请求，预警ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("获取预警详情请求参数验证失败: 预警ID无效");
                return Result.validationError("预警ID无效");
            }
            
            // 调用业务层获取预警详情
            Warning warning = warningService.getWarningById(id);
            
            LoggerUtil.info("获取预警详情成功，预警ID: {}", id);
            return Result.success(warning);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取预警详情失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取预警详情异常: " + e.getMessage(), e);
            return Result.error("获取预警详情失败，请稍后重试");
        }
    }

    /**
     * 更新预警接口
     * 
     * 更新预警的描述和结束时间
     * 
     * @param id 预警ID（必填）
     * @param description 预警描述（可选）
     * @param endTime 预警结束时间（可选，格式: yyyy-MM-dd HH:mm:ss）
     * @return 更新后的预警信息
     */
    /**
     * 更新预警接口
     * 更新预警的描述和结束时间
     */
    @PutMapping("/{id}")
    public Result<Warning> updateWarning(
            @PathVariable @NotNull(message = "预警ID不能为空") Long id,
            @RequestBody WarningUpdateRequest request) { // 👈 这里改成了 @RequestBody

        LoggerUtil.info("收到更新预警请求，预警ID: {}", id);

        try {
            if (id == null || id <= 0) {
                LoggerUtil.warn("更新预警请求参数验证失败: 预警ID无效");
                return Result.validationError("预警ID无效");
            }

            // 调用业务层更新预警
            Warning updatedWarning = warningService.updateWarning(
                    id,
                    request.getDescription(),
                    request.getEndTime()
            );

            LoggerUtil.info("更新预警成功，预警ID: {}", id);
            return Result.success("预警更新成功", updatedWarning);

        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("更新预警失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("更新预警异常: " + e.getMessage(), e);
            return Result.error("更新预警失败，请稍后重试");
        }
    }

    /**
     * 更新预警状态接口
     * 
     * 更新预警的状态（如：从活跃变为过期或取消）
     * 
     * @param id 预警ID（必填）
     * @param status 新状态（必填，ACTIVE/EXPIRED/CANCELLED）
     * @return 更新后的预警信息
     */
    @PutMapping("/{id}/status")
    public Result<Warning> updateWarningStatus(
            @PathVariable @NotNull(message = "预警ID不能为空") Long id,
            @RequestParam @NotNull(message = "预警状态不能为空") Warning.WarningStatus status) {
        
        LoggerUtil.info("收到更新预警状态请求，预警ID: {}, 新状态: {}", id, status);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("更新预警状态请求参数验证失败: 预警ID无效");
                return Result.validationError("预警ID无效");
            }
            
            if (status == null) {
                LoggerUtil.warn("更新预警状态请求参数验证失败: 预警状态为空");
                return Result.validationError("预警状态不能为空");
            }
            
            // 调用业务层更新预警状态
            Warning updatedWarning = warningService.updateWarningStatus(id, status);
            
            LoggerUtil.info("更新预警状态成功，预警ID: {}, 新状态: {}", id, status);
            return Result.success("预警状态更新成功", updatedWarning);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("更新预警状态失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("更新预警状态异常: " + e.getMessage(), e);
            return Result.error("更新预警状态失败，请稍后重试");
        }
    }

    /**
     * 取消预警接口
     * 
     * 将预警状态更新为取消
     * 
     * @param id 预警ID（必填）
     * @return 更新后的预警信息
     */
    @PostMapping("/{id}/cancel")
    public Result<Warning> cancelWarning(
            @PathVariable @NotNull(message = "预警ID不能为空") Long id) {
        
        LoggerUtil.info("收到取消预警请求，预警ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("取消预警请求参数验证失败: 预警ID无效");
                return Result.validationError("预警ID无效");
            }
            
            // 调用业务层取消预警
            Warning cancelledWarning = warningService.cancelWarning(id);
            
            LoggerUtil.info("取消预警成功，预警ID: {}", id);
            return Result.success("预警取消成功", cancelledWarning);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("取消预警失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("取消预警异常: " + e.getMessage(), e);
            return Result.error("取消预警失败，请稍后重试");
        }
    }

    /**
     * 删除预警接口
     * 
     * 从数据库中删除指定的预警记录
     * 
     * @param id 预警ID（必填）
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteWarning(
            @PathVariable @NotNull(message = "预警ID不能为空") Long id) {
        
        LoggerUtil.info("收到删除预警请求，预警ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("删除预警请求参数验证失败: 预警ID无效");
                return Result.validationError("预警ID无效");
            }
            
            // 调用业务层删除预警
            warningService.deleteWarning(id);
            
            LoggerUtil.info("删除预警成功，预警ID: {}", id);
            return Result.success();
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("删除预警失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("删除预警异常: " + e.getMessage(), e);
            return Result.error("删除预警失败，请稍后重试");
        }
    }

    /**
     * 按地区查询预警接口
     * 
     * 获取指定地区的所有预警信息
     * 
     * @param region 地区（必填）
     * @return 该地区的预警列表
     */
    @GetMapping("/region/{region}")
    public Result<List<Warning>> getWarningsByRegion(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到按地区查询预警请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("按地区查询预警请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层按地区查询预警
            List<Warning> warnings = warningService.getWarningsByRegion(region);
            
            LoggerUtil.info("按地区查询预警成功，地区: {}, 预警数: {}", region, warnings.size());
            return Result.success(warnings);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按地区查询预警失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按地区查询预警异常: " + e.getMessage(), e);
            return Result.error("按地区查询预警失败，请稍后重试");
        }
    }

    /**
     * 获取活跃预警列表接口
     * 
     * 获取所有状态为活跃且未过期的预警信息
     * 
     * @return 活跃预警列表
     */
    @GetMapping("/active")
    public Result<List<Warning>> getActiveWarnings() {
        
        LoggerUtil.info("收到获取活跃预警列表请求");
        
        try {
            // 调用业务层获取活跃预警
            List<Warning> activeWarnings = warningService.getActiveWarnings();
            
            LoggerUtil.info("获取活跃预警列表成功，活跃预警数: {}", activeWarnings.size());
            return Result.success(activeWarnings);
            
        } catch (Exception e) {
            LoggerUtil.error("获取活跃预警列表异常: " + e.getMessage(), e);
            return Result.error("获取活跃预警列表失败，请稍后重试");
        }
    }

    /**
     * 检查地区是否有活跃预警接口
     * 
     * 检查指定地区是否存在活跃的预警信息
     * 
     * @param region 地区（必填）
     * @return 是否有活跃预警
     */
    @GetMapping("/check/{region}")
    public Result<Boolean> hasActiveWarnings(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到检查地区是否有活跃预警请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("检查地区活跃预警请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层检查是否有活跃预警
            boolean hasActive = warningService.hasActiveWarnings(region);
            
            LoggerUtil.info("检查地区活跃预警完成，地区: {}, 是否有活跃预警: {}", region, hasActive);
            return Result.success(hasActive);
            
        } catch (Exception e) {
            LoggerUtil.error("检查地区活跃预警异常: " + e.getMessage(), e);
            return Result.error("检查地区活跃预警失败，请稍后重试");
        }
    }

    /**
     * 获取地区最严重的活跃预警接口
     * 
     * 获取指定地区最严重的活跃预警信息
     * 
     * @param region 地区（必填）
     * @return 最严重的活跃预警，如果没有则返回null
     */
    @GetMapping("/most-severe/{region}")
    public Result<Warning> getMostSevereActiveWarning(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到获取地区最严重活跃预警请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("获取最严重活跃预警请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层获取最严重的活跃预警
            Warning mostSevere = warningService.getMostSevereActiveWarning(region);
            
            if (mostSevere == null) {
                LoggerUtil.info("地区没有活跃预警，地区: {}", region);
                return Result.success("该地区没有活跃预警", null);
            }
            
            LoggerUtil.info("获取最严重活跃预警成功，地区: {}, 预警ID: {}, 等级: {}", region, mostSevere.getId(), mostSevere.getSeverity());
            return Result.success(mostSevere);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取最严重活跃预警失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取最严重活跃预警异常: " + e.getMessage(), e);
            return Result.error("获取最严重活跃预警失败，请稍后重试");
        }
    }

    /**
     * 获取地区预警统计信息接口
     * 
     * 获取指定地区的预警统计信息（包括不同状态和等级的预警数量）
     * 
     * @param region 地区（必填）
     * @return 预警统计信息
     */
    @GetMapping("/statistics/{region}")
    public Result<String> getWarningStatistics(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到获取地区预警统计信息请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("获取预警统计信息请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层获取预警统计信息
            String statistics = warningService.getWarningStatistics(region);
            
            LoggerUtil.info("获取预警统计信息成功，地区: {}", region);
            return Result.success(statistics);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取预警统计信息失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取预警统计信息异常: " + e.getMessage(), e);
            return Result.error("获取预警统计信息失败，请稍后重试");
        }
    }
}
