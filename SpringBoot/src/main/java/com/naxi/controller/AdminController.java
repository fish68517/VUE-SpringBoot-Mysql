package com.naxi.controller;

import com.naxi.common.ApiResponse;
import com.naxi.entity.AdminAuditLog;
import com.naxi.entity.Permission;
import com.naxi.entity.Role;
import com.naxi.entity.SystemLog;
import com.naxi.entity.SystemSetting;
import com.naxi.entity.User;
import com.naxi.service.SystemService;
import com.naxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    /**
     * 获取系统日志
     * 需求: 24.1
     */
    @GetMapping("/logs")
    public ApiResponse<?> getSystemLogs(
            @RequestParam(required = false) String logLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SystemLog> logsPage;

            if (logLevel != null && !logLevel.isEmpty()) {
                try {
                    SystemLog.LogLevel level = SystemLog.LogLevel.valueOf(logLevel);
                    logsPage = systemService.getSystemLogs(level, pageable);
                } catch (IllegalArgumentException e) {
                    return ApiResponse.error(400, "无效的日志级别");
                }
            } else {
                logsPage = systemService.getSystemLogs(null, pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", logsPage.getContent());
            response.put("totalElements", logsPage.getTotalElements());
            response.put("totalPages", logsPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);

            return ApiResponse.success("获取系统日志成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取系统日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统参数
     * 需求: 24.1
     */
    @GetMapping("/settings")
    public ApiResponse<?> getSystemSettings() {
        try {
            List<SystemSetting> settings = systemService.getAllSystemSettings();
            return ApiResponse.success("获取系统参数成功", settings);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取系统参数失败: " + e.getMessage());
        }
    }

    /**
     * 更新系统参数
     * 需求: 24.1
     */
    @PutMapping("/settings")
    public ApiResponse<?> updateSystemSettings(@RequestBody Map<String, String> request) {
        try {
            String settingKey = request.get("settingKey");
            String settingValue = request.get("settingValue");
            String description = request.get("description");

            if (settingKey == null || settingKey.trim().isEmpty()) {
                return ApiResponse.error(400, "参数key不能为空");
            }
            if (settingValue == null || settingValue.trim().isEmpty()) {
                return ApiResponse.error(400, "参数值不能为空");
            }

            SystemSetting updatedSetting = systemService.updateSystemSetting(settingKey, settingValue, description);
            return ApiResponse.success("更新系统参数成功", updatedSetting);
        } catch (Exception e) {
            return ApiResponse.error(500, "更新系统参数失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户列表
     * 需求: 24.1
     */
    @GetMapping("/users")
    public ApiResponse<?> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取所有用户（实际应该使用分页查询，这里简化处理）
            List<User> users = userService.getAllUsers();
            
            // 手动分页处理
            int start = page * size;
            int end = Math.min(start + size, users.size());
            List<User> pageContent = users.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("content", pageContent);
            response.put("totalElements", users.size());
            response.put("totalPages", (users.size() + size - 1) / size);
            response.put("currentPage", page);
            response.put("pageSize", size);

            return ApiResponse.success("获取用户列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 禁用/启用用户
     * 需求: 24.1
     */
    @PutMapping("/users/{id}/status")
    public ApiResponse<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");

            if (status == null || status.trim().isEmpty()) {
                return ApiResponse.error(400, "状态不能为空");
            }

            User user;
            if ("disabled".equalsIgnoreCase(status)) {
                user = userService.disableUser(id);
            } else if ("active".equalsIgnoreCase(status)) {
                user = userService.enableUser(id);
            } else {
                return ApiResponse.error(400, "无效的状态值");
            }

            return ApiResponse.success("更新用户状态成功", user);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取管理员操作日志
     * 需求: 24.2
     */
    @GetMapping("/audit-logs")
    public ApiResponse<?> getAdminAuditLogs(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) String operationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AdminAuditLog> auditLogsPage = systemService.getAdminAuditLogs(adminId, operationType, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", auditLogsPage.getContent());
            response.put("totalElements", auditLogsPage.getTotalElements());
            response.put("totalPages", auditLogsPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);

            return ApiResponse.success("获取管理员操作日志成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取管理员操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色列表
     * 需求: 24.2
     */
    @GetMapping("/roles")
    public ApiResponse<?> getRoleList() {
        try {
            List<Role> roles = systemService.getAllRoles();
            return ApiResponse.success("获取角色列表成功", roles);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建角色
     * 需求: 24.2
     */
    @PostMapping("/roles")
    public ApiResponse<?> createRole(@RequestBody Map<String, String> request) {
        try {
            String roleName = request.get("roleName");
            String description = request.get("description");

            if (roleName == null || roleName.trim().isEmpty()) {
                return ApiResponse.error(400, "角色名称不能为空");
            }

            Role createdRole = systemService.createRole(roleName, description);
            return ApiResponse.success("创建角色成功", createdRole);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建角色失败: " + e.getMessage());
        }
    }

    /**
     * 更新角色权限
     * 需求: 24.2
     */
    @PutMapping("/roles/{id}")
    public ApiResponse<?> updateRolePermissions(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> permissionNames = (List<String>) request.get("permissionNames");

            if (permissionNames == null) {
                return ApiResponse.error(400, "权限列表不能为空");
            }

            systemService.updateRolePermissions(id, permissionNames);
            
            // 返回更新后的角色和权限信息
            Role role = systemService.getRoleById(id);
            List<Permission> permissions = systemService.getRolePermissions(id);

            Map<String, Object> response = new HashMap<>();
            response.put("role", role);
            response.put("permissions", permissions);

            return ApiResponse.success("更新角色权限成功", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新角色权限失败: " + e.getMessage());
        }
    }
}
