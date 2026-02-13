package com.naxi.controller;

import com.naxi.common.ApiResponse;
import com.naxi.entity.Collection;
import com.naxi.entity.OperationLog;
import com.naxi.entity.User;
import com.naxi.service.CollectionService;
import com.naxi.service.OperationLogService;
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
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private CollectionService collectionService;

    /**
     * 用户注册
     * 需求: 18.1
     */
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");

            if (username == null || username.trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (email == null || email.trim().isEmpty()) {
                return ApiResponse.error(400, "邮箱不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }

            User registeredUser = userService.register(username, email, password);
            return ApiResponse.success("注册成功", registeredUser);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     * 需求: 18.1
     */
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || username.trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }

            User user = userService.login(username, password);
            return ApiResponse.success("登录成功", user);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     * 需求: 18.2
     */
    @GetMapping("/{id}")
    public ApiResponse<?> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return ApiResponse.success(user);
            }
            return ApiResponse.error(1001, "用户不存在");
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * 需求: 18.2
     */
    @PutMapping("/{id}")
    public ApiResponse<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nickname = request.get("nickname");
            String avatarUrl = request.get("avatarUrl");
            String bio = request.get("bio");

            User updatedUser = userService.updateUserInfo(id, nickname, avatarUrl, bio);
            return ApiResponse.success("更新成功", updatedUser);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     * 需求: 18.2
     */
    @PutMapping("/{id}/password")
    public ApiResponse<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return ApiResponse.error(400, "旧密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ApiResponse.error(400, "新密码不能为空");
            }

            userService.changePassword(id, oldPassword, newPassword);
            return ApiResponse.success("密码修改成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户操作历史
     * 需求: 18.2
     */
    @GetMapping("/{id}/history")
    public ApiResponse<?> getHistory(@PathVariable Long id,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        try {
            // 验证用户是否存在
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error(1001, "用户不存在");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<OperationLog> historyPage = operationLogService.getUserOperationHistory(id, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", historyPage.getContent());
            response.put("totalElements", historyPage.getTotalElements());
            response.put("totalPages", historyPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);

            return ApiResponse.success("获取历史成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取操作历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏列表
     * 需求: 18.2
     */
    @GetMapping("/{id}/collections")
    public ApiResponse<?> getCollections(@PathVariable Long id) {
        try {
            // 验证用户是否存在
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error(1001, "用户不存在");
            }

            List<Collection> collections = collectionService.getUserCollections(id);
            return ApiResponse.success("获取收藏成功", collections);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取收藏列表失败: " + e.getMessage());
        }
    }
}
