package com.health.controller;

import com.health.dto.UserUpdateRequest;
import com.health.entity.User;
import com.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户个人信息管理和用户列表查询请求
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取个人信息端点
     * GET /api/users/profile
     * 
     * 根据用户ID获取用户的个人信息
     * 需要在请求头或参数中传递用户ID
     *
     * @param userId 用户ID（从请求参数或会话中获取）
     * @return 用户个人信息
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestParam Long userId) {
        try {
            User user = userService.getUserInfo(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取个人信息成功");
            response.put("data", user);
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
     * 更新个人信息端点
     * PUT /api/users/profile
     * 
     * 更新用户的个人信息（邮箱、姓名、年龄、性别、电话）
     * 验证信息格式后保存到数据库
     *
     * @param userId 用户ID（从请求参数或会话中获取）
     * @param updateRequest 用户信息更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestParam Long userId, @RequestBody UserUpdateRequest updateRequest) {
        try {
            User updatedUser = userService.updateUserInfo(userId, updateRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新个人信息成功");
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
     * 获取用户列表端点
     * GET /api/users
     * 
     * 获取所有用户列表（仅管理员可访问）
     * 返回所有活跃用户的信息
     *
     * @param role 用户角色（用于权限检查，应为ADMIN）
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<?> getUserList(@RequestParam(required = false) String role) {
        try {
            // 权限检查：只有管理员可以访问用户列表
            if (role == null || !role.equals("ADMIN")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 403);
                response.put("message", "权限不足，只有管理员可以访问用户列表");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            List<User> users = userService.getAllActiveUsers();
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
}
