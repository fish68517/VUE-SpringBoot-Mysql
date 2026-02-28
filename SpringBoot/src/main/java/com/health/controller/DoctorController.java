package com.health.controller;

import com.health.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医师控制器
 * 处理医师相关的请求，包括患者列表、患者档案查看等
 */
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 获取患者列表端点
     * GET /api/doctors/patients
     * 
     * 获取当前医师的所有患者列表
     * 通过咨询记录和健康建议找到关联的患者
     *
     * @param doctorId 医师ID（从请求参数或会话中获取）
     * @return 患者列表
     */
    @GetMapping("/patients")
    public ResponseEntity<?> getDoctorPatients(@RequestParam Long doctorId) {
        try {
            List<Map<String, Object>> patients = doctorService.getDoctorPatients(doctorId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取患者列表成功");
            response.put("data", patients);
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
     * 获取患者档案端点
     * GET /api/doctors/patients/{patientId}
     * 
     * 获取特定患者的完整健康档案
     * 包含患者个人信息、健康数据、咨询记录和健康建议
     * 医师只能查看自己的患者档案
     *
     * @param doctorId 医师ID（从请求参数或会话中获取）
     * @param patientId 患者ID（从路径参数中获取）
     * @return 患者档案信息
     */
    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> getPatientRecord(@RequestParam Long doctorId, @PathVariable Long patientId) {
        try {
            Map<String, Object> patientRecord = doctorService.getPatientRecord(doctorId, patientId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取患者档案成功");
            response.put("data", patientRecord);
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
     * 获取所有医师端点
     * GET /api/doctors
     * 
     * 获取系统中所有医师的信息
     * 仅管理员可以访问此端点
     *
     * @param role 用户角色（用于权限检查，应为ADMIN）
     * @return 医师列表
     */
    @GetMapping
    public ResponseEntity<?> getAllDoctors(@RequestParam(required = false) String role) {
        try {
            // 权限检查：只有管理员可以访问医师列表
            if (role == null || !role.equals("ADMIN")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 403);
                response.put("message", "权限不足，只有管理员可以访问医师列表");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            List<Map<String, Object>> doctors = doctorService.getAllDoctors();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取医师列表成功");
            response.put("data", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
