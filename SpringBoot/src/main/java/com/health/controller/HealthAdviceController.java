package com.health.controller;

import com.health.dto.HealthAdviceRequest;
import com.health.entity.HealthAdvice;
import com.health.service.HealthAdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康建议控制器
 * 处理医师创建和患者查看健康建议的相关请求
 */
@RestController
@RequestMapping("/health-advice")
public class HealthAdviceController {

    @Autowired
    private HealthAdviceService healthAdviceService;

    /**
     * 创建健康建议端点
     * POST /api/health-advice
     *
     * @param doctorId 医师ID（从请求头或会话中获取）
     * @param healthAdviceRequest 健康建议请求信息
     * @return 创建的健康建议
     */
    @PostMapping
    public ResponseEntity<?> createHealthAdvice(
            @RequestParam Long doctorId,
            @RequestBody HealthAdviceRequest healthAdviceRequest) {
        try {
            // 验证请求参数
            if (healthAdviceRequest.getPatientId() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "患者ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (healthAdviceRequest.getAdviceContent() == null || healthAdviceRequest.getAdviceContent().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "建议内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 创建健康建议
            HealthAdvice healthAdvice = healthAdviceService.createHealthAdvice(
                    doctorId,
                    healthAdviceRequest.getPatientId(),
                    healthAdviceRequest.getAdviceContent(),
                    healthAdviceRequest.getRecommendation()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "健康建议创建成功");
            response.put("data", healthAdvice);
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
     * 获取患者健康建议端点
     * GET /api/health-advice/patient/{patientId}
     *
     * @param patientId 患者ID
     * @return 患者的健康建议列表
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientHealthAdvice(@PathVariable Long patientId) {
        try {
            // 获取患者的健康建议
            List<HealthAdvice> healthAdvices = healthAdviceService.getPatientHealthAdvice(patientId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取患者健康建议成功");
            response.put("data", healthAdvices);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取我的健康建议端点
     * GET /api/health-advice/my-advice
     *
     * @param userId 用户ID（从请求头或会话中获取）
     * @return 当前用户的健康建议列表
     */
    @GetMapping("/my-advice")
    public ResponseEntity<?> getMyHealthAdvice(@RequestParam Long userId) {
        try {
            // 获取当前用户的健康建议
            List<HealthAdvice> healthAdvices = healthAdviceService.getPatientHealthAdvice(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取我的健康建议成功");
            response.put("data", healthAdvices);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
