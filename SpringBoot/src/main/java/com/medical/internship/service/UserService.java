package com.medical.internship.service;

import com.medical.internship.dto.UserRegisterRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.dto.UserUpdateRequest;
import com.medical.internship.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    UserResponse register(UserRegisterRequest request);
    
    /**
     * 用户登录
     */
    UserResponse login(String username, String password);
    
    /**
     * 获取用户信息
     */
    UserResponse getUserProfile(Long userId);
    
    /**
     * 更新用户信息
     */
    UserResponse updateUserProfile(Long userId, UserUpdateRequest request);
    
    /**
     * 根据用户ID获取用户实体
     */
    User getUserById(Long userId);
    
    /**
     * 根据用户名获取用户实体
     */
    User getUserByUsername(String username);
    
    /**
     * 获取待认证学生列表
     */
    java.util.List<UserResponse> getPendingStudents(Long schoolId);
    
    /**
     * 审批学生认证
     */
    UserResponse approveStudent(Long userId, Long schoolId);
    
    /**
     * 驳回学生认证
     */
    UserResponse rejectStudent(Long userId, Long schoolId);
    
    /**
     * 获取所有待认证用户（系统管理员）
     */
    java.util.List<UserResponse> getPendingUsers();
    
    /**
     * 审批用户认证（系统管理员）
     */
    UserResponse approveUserAuth(Long userId);
    
    /**
     * 驳回用户认证（系统管理员）
     */
    UserResponse rejectUserAuth(Long userId);
}
