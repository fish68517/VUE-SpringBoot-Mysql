package com.health.controller;

import com.health.entity.HealthData;
import com.health.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康数据控制器
 * 处理健康数据的提交、查询、趋势分析等请求
 */
@RestController
@RequestMapping("/health-data")
public class HealthDataController {

    @Autowired
    private HealthDataService healthDataService;

    /**
     * 提交健康数据端点
     * POST /api/health-data
     * 
     * 用户提交健康数据（身高、体重、血压、心率、饮食、运动等）
     * 系统验证数据范围后保存到数据库
     *
     * @param healthData 健康数据对象
     * @return 保存后的健康数据
     */
    @PostMapping
    public ResponseEntity<?> submitHealthData(@RequestBody HealthData healthData) {
        try {
            // 验证必要字段
            if (healthData.getUserId() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 保存健康数据
            HealthData savedData = healthDataService.saveHealthData(healthData);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "健康数据提交成功");
            response.put("data", savedData);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取用户健康数据端点
     * GET /api/health-data
     * 
     * 获取用户的所有健康数据记录
     *
     * @param userId 用户ID
     * @return 用户的健康数据列表
     */
    @GetMapping
    public ResponseEntity<?> getUserHealthData(@RequestParam Long userId) {
        try {
            // 验证用户ID
            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取用户健康数据
            List<HealthData> healthDataList = healthDataService.getUserHealthData(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取健康数据成功");
            response.put("data", healthDataList);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取健康趋势端点
     * GET /api/health-data/trends
     * 
     * 获取用户健康数据的统计分析和趋势信息
     * 包括平均值、最大值、最小值等统计数据
     *
     * @param userId 用户ID
     * @return 健康数据统计信息
     */
    @GetMapping("/trends")
    public ResponseEntity<?> getHealthTrends(@RequestParam Long userId) {
        try {
            // 验证用户ID
            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取健康数据统计
            HealthDataService.HealthDataStatistics statistics = healthDataService.getHealthDataStatistics(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取健康趋势成功");
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 按时间范围查询端点
     * GET /api/health-data/range
     * 
     * 按时间范围查询用户的健康数据
     * 支持按开始时间和结束时间筛选
     *
     * @param userId 用户ID
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     * @return 时间范围内的健康数据列表
     */
    @GetMapping("/range")
    public ResponseEntity<?> getHealthDataByRange(
            @RequestParam Long userId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            // 验证用户ID
            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证时间参数
            if (startTime == null || startTime.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "开始时间不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (endTime == null || endTime.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "结束时间不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 解析时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);

            // 获取时间范围内的健康数据
            List<HealthData> healthDataList = healthDataService.getHealthDataByTimeRange(userId, start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "按时间范围查询成功");
            response.put("data", healthDataList);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
