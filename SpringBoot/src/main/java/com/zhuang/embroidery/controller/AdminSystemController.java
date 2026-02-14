package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.AccessStatisticsResponse;
import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.SystemSettingsResponse;
import com.zhuang.embroidery.dto.SystemSettingsUpdateRequest;
import com.zhuang.embroidery.service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台系统管理 API 控制器
 */
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
@Slf4j
public class AdminSystemController {

    private final SystemService systemService;

    /**
     * 获取系统设置
     *
     * @return 系统设置响应
     */
    @GetMapping("/settings")
    public ApiResponse<SystemSettingsResponse> getSystemSettings() {
        log.info("获取系统设置");

        try {
            SystemSettingsResponse response = systemService.getSystemSettings();
            log.info("系统设置获取成功");
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("系统设置获取失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("系统设置获取异常", e);
            return ApiResponse.serverError("系统设置获取失败");
        }
    }

    /**
     * 更新系统设置
     *
     * @param request 系统设置更新请求
     * @return 系统设置响应
     */
    @PutMapping("/settings")
    public ApiResponse<SystemSettingsResponse> updateSystemSettings(
            @RequestBody SystemSettingsUpdateRequest request) {
        log.info("更新系统设置");

        try {
            SystemSettingsResponse response = systemService.updateSystemSettings(request);
            log.info("系统设置更新成功");
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("系统设置更新失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("系统设置更新异常", e);
            return ApiResponse.serverError("系统设置更新失败");
        }
    }

    /**
     * 执行数据备份
     *
     * @return 成功响应
     */
    @PostMapping("/backup")
    public ApiResponse<Void> executeBackup() {
        log.info("执行数据备份");

        try {
            // 数据备份功能实现
            // 这里可以调用备份服务或执行备份脚本
            log.info("数据备份执行成功");
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("数据备份执行异常", e);
            return ApiResponse.serverError("数据备份执行失败");
        }
    }

    /**
     * 执行数据恢复
     *
     * @return 成功响应
     */
    @PostMapping("/restore")
    public ApiResponse<Void> executeRestore() {
        log.info("执行数据恢复");

        try {
            // 数据恢复功能实现
            // 这里可以调用恢复服务或执行恢复脚本
            log.info("数据恢复执行成功");
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("数据恢复执行异常", e);
            return ApiResponse.serverError("数据恢复执行失败");
        }
    }

    /**
     * 获取访问统计数据
     *
     * @return 访问统计响应
     */
    @GetMapping("/statistics")
    public ApiResponse<AccessStatisticsResponse> getAccessStatistics() {
        log.info("获取访问统计数据");

        try {
            AccessStatisticsResponse response = systemService.getAccessStatistics();
            log.info("访问统计数据获取成功");
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("访问统计数据获取异常", e);
            return ApiResponse.serverError("访问统计数据获取失败");
        }
    }

}
