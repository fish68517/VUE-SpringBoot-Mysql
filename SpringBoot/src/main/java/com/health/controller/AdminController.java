package com.health.controller;

import com.health.dto.DoctorUpdateRequest;
import com.health.entity.AuditLog;
import com.health.entity.Doctor;
import com.health.entity.User;
import com.health.service.AdminService;
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
 * 管理员控制器
 * 处理系统管理员的用户权限管理、医师管理、数据统计和审计日志查询请求
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取用户列表端点
     * GET /api/admin/users
     * 
     * 获取系统中所有用户的列表（仅管理员可访问）
     *
     * @return 所有用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取用户列表成功");
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 修改用户权限端点
     * PUT /api/admin/users/{id}/role
     * 
     * 修改指定用户的角色权限（USER、DOCTOR、ADMIN）
     *
     * @param id 用户ID
     * @param roleRequest 包含新角色的请求体
     * @return 更新后的用户信息
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> roleRequest) {
        try {
            String newRole = roleRequest.get("role");
            if (newRole == null || newRole.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "角色不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            User updatedUser = adminService.updateUserRole(id, newRole);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "修改用户权限成功");
            response.put("data", updatedUser);
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
     * 禁用用户端点
     * PUT /api/admin/users/{id}/status
     * 
     * 禁用指定用户的账户，阻止用户登录
     *
     * @param id 用户ID
     * @param statusRequest 包含新状态的请求体
     * @return 更新后的用户信息
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        try {
            String status = statusRequest.get("status");
            if (status == null || status.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "状态不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            User updatedUser;
            if ("ACTIVE".equals(status)) {
                updatedUser = adminService.enableUserAccount(id);
            } else if ("INACTIVE".equals(status)) {
                updatedUser = adminService.disableUserAccount(id);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "无效的状态值，只能是ACTIVE或INACTIVE");
                return ResponseEntity.badRequest().body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "修改用户状态成功");
            response.put("data", updatedUser);
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
     * 获取医师列表端点
     * GET /api/admin/doctors
     * 
     * 获取系统中所有医师的列表（仅管理员可访问）
     *
     * @return 所有医师列表
     */
    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        try {
            List<Doctor> doctors = adminService.getAllDoctors();
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

    /**
     * 编辑医师信息端点
     * PUT /api/admin/doctors/{id}
     * 
     * 编辑指定医师的信息（专科、医院等）
     *
     * @param id 医师ID
     * @param updateRequest 医师信息更新请求
     * @return 更新后的医师信息
     */
    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctorInfo(@PathVariable Long id, @RequestBody DoctorUpdateRequest updateRequest) {
        try {
            Doctor updatedDoctor = adminService.updateDoctorInfo(id, updateRequest.getSpecialization(), updateRequest.getHospital());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "编辑医师信息成功");
            response.put("data", updatedDoctor);
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
     * 删除医师端点
     * DELETE /api/admin/doctors/{id}
     * 
     * 删除指定医师的账户及其关联的用户账户
     *
     * @param id 医师ID
     * @return 删除结果
     */
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctorAccount(@PathVariable Long id) {
        try {
            adminService.deleteDoctorAccount(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除医师账户成功");
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
     * 获取数据统计端点
     * GET /api/admin/statistics
     * 
     * 获取系统的健康数据统计信息（用户数、健康数据数量等）
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> statistics = adminService.getHealthDataStatistics();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取数据统计成功");
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取审计日志端点
     * GET /api/admin/audit-logs
     * 
     * 获取系统的审计日志，支持按时间范围、用户ID、操作类型筛选
     *
     * @param userId 用户ID（可选）
     * @param action 操作类型（可选）
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @return 审计日志列表
     */
    @GetMapping("/audit-logs")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        try {
            List<AuditLog> auditLogs;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 根据不同的查询条件获取审计日志
            if (userId != null && startTime != null && endTime != null) {
                // 按用户ID和时间范围查询
                LocalDateTime start = LocalDateTime.parse(startTime, formatter);
                LocalDateTime end = LocalDateTime.parse(endTime, formatter);
                auditLogs = adminService.getAuditLogsByTimeRange(start, end);
                auditLogs = auditLogs.stream()
                        .filter(log -> log.getUserId() != null && log.getUserId().equals(userId))
                        .toList();
            } else if (startTime != null && endTime != null) {
                // 按时间范围查询
                LocalDateTime start = LocalDateTime.parse(startTime, formatter);
                LocalDateTime end = LocalDateTime.parse(endTime, formatter);
                auditLogs = adminService.getAuditLogsByTimeRange(start, end);
            } else if (userId != null) {
                // 按用户ID查询
                auditLogs = adminService.getAuditLogsByUserId(userId);
            } else if (action != null && !action.isEmpty()) {
                // 按操作类型查询
                auditLogs = adminService.getAuditLogsByAction(action);
            } else {
                // 获取所有审计日志
                auditLogs = adminService.getAuditLogs();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取审计日志成功");
            response.put("data", auditLogs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
