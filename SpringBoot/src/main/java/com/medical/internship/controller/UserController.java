package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.UserLoginRequest;
import com.medical.internship.dto.UserRegisterRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.dto.UserUpdateRequest;
import com.medical.internship.interceptor.SessionInterceptor;
import com.medical.internship.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());
        UserResponse response = userService.register(request);
        return ApiResponse.success("注册成功", response);
    }
    
    /**
     * 用户登录
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody UserLoginRequest request, HttpSession session) {
        log.info("用户登录请求: {}", request.getUsername());
        UserResponse user = userService.login(request.getUsername(), request.getPassword());
        
        // 将用户信息存储到会话中
        SessionInterceptor.storeUserInSession(session, user);
        
        return ApiResponse.success("登录成功", user);
    }
    
    /**
     * 获取个人信息
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile() {
        Long userId = SessionContext.getCurrentUserId();
        log.info("获取用户信息: {}", userId);
        UserResponse response = userService.getUserProfile(userId);
        return ApiResponse.success(response);
    }
    
    /**
     * 更新个人信息
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        Long userId = SessionContext.getCurrentUserId();
        log.info("更新用户信息: {}", userId);
        UserResponse response = userService.updateUserProfile(userId, request);
        return ApiResponse.success("更新成功", response);
    }
    
    /**
     * 用户登出
     * POST /api/users/logout
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpSession session) {
        Long userId = SessionContext.getCurrentUserId();
        log.info("用户登出: {}", userId);
        SessionInterceptor.clearUserFromSession(session);
        return ApiResponse.success();
    }
    
    /**
     * 获取待认证学生列表（学校管理员）
     * GET /api/users/pending-students
     */
    @GetMapping("/pending-students")
    public ApiResponse<java.util.List<UserResponse>> getPendingStudents() {
        Long schoolId = SessionContext.getCurrentOrganizationId();
        log.info("获取待认证学生列表: 学校ID={}", schoolId);
        java.util.List<UserResponse> students = userService.getPendingStudents(schoolId);
        return ApiResponse.success(students);
    }
    
    /**
     * 审批学生认证（学校管理员）
     * PUT /api/users/{id}/approve
     */
    @PutMapping("/{id}/approve")
    public ApiResponse<UserResponse> approveStudent(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        Long schoolId = SessionContext.getCurrentOrganizationId();
        log.info("审批学生认证: 学生ID={}, 学校ID={}", id, schoolId);
        UserResponse response = userService.approveStudent(id, schoolId);
        return ApiResponse.success("认证通过", response);
    }
    
    /**
     * 驳回学生认证（学校管理员）
     * PUT /api/users/{id}/reject
     */
    @PutMapping("/{id}/reject")
    public ApiResponse<UserResponse> rejectStudent(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        Long schoolId = SessionContext.getCurrentOrganizationId();
        String reason = request.get("reason");
        log.info("驳回学生认证: 学生ID={}, 学校ID={}, 原因={}", id, schoolId, reason);
        UserResponse response = userService.rejectStudent(id, schoolId);
        return ApiResponse.success("已驳回", response);
    }
    
    /**
     * 获取所有待认证用户（系统管理员）
     * GET /api/users/pending-auth
     */
    @GetMapping("/pending-auth")
    public ApiResponse<java.util.List<UserResponse>> getPendingUsers() {
        log.info("获取待认证用户列表");
        verifyAdminRole();
        java.util.List<UserResponse> users = userService.getPendingUsers();
        return ApiResponse.success(users);
    }
    
    /**
     * 审批用户认证（系统管理员）
     * PUT /api/users/{id}/approve-auth
     */
    @PutMapping("/{id}/approve-auth")
    public ApiResponse<UserResponse> approveUserAuth(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        log.info("审批用户认证: 用户ID={}", id);
        verifyAdminRole();
        UserResponse response = userService.approveUserAuth(id);
        return ApiResponse.success("认证通过", response);
    }
    
    /**
     * 驳回用户认证（系统管理员）
     * PUT /api/users/{id}/reject-auth
     */
    @PutMapping("/{id}/reject-auth")
    public ApiResponse<UserResponse> rejectUserAuth(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        String reason = request.get("reason");
        log.info("驳回用户认证: 用户ID={}, 原因={}", id, reason);
        verifyAdminRole();
        UserResponse response = userService.rejectUserAuth(id);
        return ApiResponse.success("已驳回", response);
    }
    
    /**
     * 验证当前用户是否为系统管理员
     */
    private void verifyAdminRole() {
        String role = SessionContext.getCurrentUserRole();
        if (!"ADMIN".equals(role)) {
            throw new com.medical.internship.common.AccessDeniedException("只有系统管理员可以访问此接口");
        }
    }
}
