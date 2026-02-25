package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.User;
import com.tourism.service.UserService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    private static final Logger logger = LoggerUtil.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册端点
     * @param request 注册请求体
     * @return API响应
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            LoggerUtil.info(logger, "处理用户注册请求 - 用户名: " + request.getUsername());
            
            User user = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getPhone()
            );
            
            // 返回用户信息（不包含密码）
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole());
            response.put("status", user.getStatus());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "用户注册失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录端点
     * @param request 登录请求体
     * @return API响应
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            LoggerUtil.info(logger, "处理用户登录请求 - 用户名: " + request.getUsername());
            
            User user = userService.login(
                request.getUsername(),
                request.getPassword()
            );
            
            // 返回用户信息（不包含密码）
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("realName", user.getRealName());
            response.put("role", user.getRole());
            response.put("status", user.getStatus());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "用户登录失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户个人信息端点
     * @param id 用户ID
     * @return API响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getProfile(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理获取用户信息请求 - ID: " + id);
            
            User user = userService.getUserProfile(id);
            
            // 返回用户信息（不包含密码）
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("realName", user.getRealName());
            response.put("role", user.getRole());
            response.put("status", user.getStatus());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户信息失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户个人信息端点
     * @param id 用户ID
     * @param request 更新请求体
     * @return API响应
     */
    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新用户信息请求 - ID: " + id);
            
            User user = userService.updateUserProfile(
                id,
                request.getRealName(),
                request.getEmail(),
                request.getPhone()
            );
            
            // 返回更新后的用户信息（不包含密码）
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("realName", user.getRealName());
            response.put("role", user.getRole());
            response.put("status", user.getStatus());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新用户信息失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新个人信息请求体
     */
    public static class UpdateProfileRequest {
        private String realName;
        private String email;
        private String phone;
        
        public String getRealName() {
            return realName;
        }
        
        public void setRealName(String realName) {
            this.realName = realName;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
    
    /**
     * 注册请求体
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String phone;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
    
    /**
     * 登录请求体
     */
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    /**
     * 获取用户列表端点（管理员）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/admin/list")
    public ApiResponse<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取用户列表请求 - 页码: " + page + ", 每页数量: " + size);
            
            org.springframework.data.domain.Page<User> userPage = userService.getUserList(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", userPage.getContent());
            response.put("total", userPage.getTotalElements());
            response.put("currentPage", userPage.getNumber());
            response.put("pageSize", userPage.getSize());
            response.put("totalPages", userPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 禁用用户端点（管理员）
     * @param id 用户ID
     * @return API响应
     */
    @PutMapping("/admin/{id}/disable")
    public ApiResponse<Map<String, Object>> disableUser(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理禁用用户请求 - ID: " + id);
            
            User user = userService.disableUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("status", user.getStatus());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "禁用用户失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 启用用户端点（管理员）
     * @param id 用户ID
     * @return API响应
     */
    @PutMapping("/admin/{id}/enable")
    public ApiResponse<Map<String, Object>> enableUser(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理启用用户请求 - ID: " + id);
            
            User user = userService.enableUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("status", user.getStatus());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "启用用户失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户端点（管理员）
     * @param id 用户ID
     * @return API响应
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理删除用户请求 - ID: " + id);
            
            userService.deleteUser(id);
            
            return ApiResponse.success("用户删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除用户失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
