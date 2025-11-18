package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册后的用户
     */
    User register(User user);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录用户信息
     */
    User login(String username, String password);
    
    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param user 更新的用户信息
     * @return 更新后的用户信息
     */
    User updateUser(Long userId, User user);
    
    /**
     * 查询所有用户列表（分页）
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> getAllUsers(Pageable pageable);
    
    /**
     * 搜索用户
     * @param keyword 搜索关键词（用户名、昵称、邮箱、手机号）
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> searchUsers(String keyword, Pageable pageable);
    
    /**
     * 启用/禁用用户
     * @param userId 用户ID
     * @param status 状态（0禁用/1启用）
     */
    void updateUserStatus(Long userId, Integer status);
}
