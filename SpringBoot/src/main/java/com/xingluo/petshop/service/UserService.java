package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.User;

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
}
