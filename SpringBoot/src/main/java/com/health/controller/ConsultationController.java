package com.health.controller;

import com.health.dto.ConsultationAnswerRequest;
import com.health.dto.ConsultationRequest;
import com.health.entity.Consultation;
import com.health.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 咨询控制器
 * 处理用户与医师之间的咨询相关请求
 */
@RestController
@RequestMapping("/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * 提交咨询端点
     * POST /api/consultations
     *
     * @param userId 用户ID（从请求头或会话中获取）
     * @param consultationRequest 咨询请求信息
     * @return 创建的咨询记录
     */
    @PostMapping
    public ResponseEntity<?> submitConsultation(
            @RequestParam Long userId,
            @RequestBody ConsultationRequest consultationRequest) {
        try {
            // 验证请求参数
            if (consultationRequest.getQuestion() == null || consultationRequest.getQuestion().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "咨询问题不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 提交咨询
            Consultation consultation = consultationService.submitConsultation(userId, consultationRequest.getQuestion());

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "咨询提交成功");
            response.put("data", consultation);
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
     * 获取咨询列表端点
     * GET /api/consultations
     *
     * @param userId 用户ID（从请求头或会话中获取）
     * @return 咨询列表
     */
    @GetMapping
    public ResponseEntity<?> getConsultationList(@RequestParam Long userId) {
        try {
            // 获取用户的咨询历史
            List<Consultation> consultations = consultationService.getConsultationHistory(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取咨询列表成功");
            response.put("data", consultations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 医师回复咨询端点
     * PUT /api/consultations/{id}/answer
     *
     * @param id 咨询ID
     * @param doctorId 医师ID（从请求头或会话中获取）
     * @param consultationAnswerRequest 回复请求信息
     * @return 更新后的咨询记录
     */
    @PutMapping("/{id}/answer")
    public ResponseEntity<?> replyConsultation(
            @PathVariable Long id,
            @RequestParam Long doctorId,
            @RequestBody ConsultationAnswerRequest consultationAnswerRequest) {
        try {
            // 验证回复内容
            if (consultationAnswerRequest.getAnswer() == null || consultationAnswerRequest.getAnswer().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "回复内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 医师回复咨询
            Consultation consultation = consultationService.replyConsultation(id, doctorId, consultationAnswerRequest.getAnswer());

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "咨询回复成功");
            response.put("data", consultation);
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
     * 获取咨询详情端点
     * GET /api/consultations/{id}
     *
     * @param id 咨询ID
     * @return 咨询详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getConsultationDetail(@PathVariable Long id) {
        try {
            // 获取咨询详情
            Consultation consultation = consultationService.getConsultationDetail(id);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取咨询详情成功");
            response.put("data", consultation);
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
