package com.tourism.service;

import com.tourism.entity.User;
import com.tourism.exception.BusinessException;
import com.tourism.repository.UserRepository;
import com.tourism.util.ValidationUtil;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * 用户业务逻辑服务
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerUtil.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param phone 手机号
     * @return 注册成功的用户对象
     */
    public User register(String username, String password, String email, String phone) {
        // 验证输入参数
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePhone(phone);
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            LoggerUtil.warn(logger, "用户注册失败：用户名已存在 - " + username);
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            LoggerUtil.warn(logger, "用户注册失败：邮箱已存在 - " + email);
            throw new BusinessException("邮箱已存在");
        }
        
        // 检查手机号是否已存在
        if (phone != null && !phone.isEmpty() && userRepository.existsByPhone(phone)) {
            LoggerUtil.warn(logger, "用户注册失败：手机号已存在 - " + phone);
            throw new BusinessException("手机号已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("tourist");
        user.setStatus("active");
        
        // 保存用户
        User savedUser = userRepository.save(user);
        LoggerUtil.info(logger, "用户注册成功 - 用户名: " + username);
        
        return savedUser;
    }
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象
     */
    public User login(String username, String password) {
        // 验证输入参数
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);
        
        // 查询用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn(logger, "用户登录失败：用户不存在 - " + username);
            throw new BusinessException("用户名或密码错误");
        }
        
        User user = userOptional.get();
        
        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            LoggerUtil.warn(logger, "用户登录失败：用户已被禁用 - " + username);
            throw new BusinessException("用户已被禁用，无法登录");
        }
        
        // 验证密码（简单的明文比对，生产环境应使用加密）
        if (!password.equals(user.getPassword())) {
            LoggerUtil.warn(logger, "用户登录失败：密码错误 - " + username);
            throw new BusinessException("用户名或密码错误");
        }
        
        LoggerUtil.info(logger, "用户登录成功 - 用户名: " + username);
        return user;
    }
    
    /**
     * 获取用户个人信息
     * @param id 用户ID
     * @return 用户对象
     */
    public User getUserProfile(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn(logger, "获取用户信息失败：用户不存在 - ID: " + id);
            throw new BusinessException("用户不存在");
        }
        
        LoggerUtil.info(logger, "获取用户信息成功 - ID: " + id);
        return userOptional.get();
    }
    
    /**
     * 更新用户个人信息
     * @param id 用户ID
     * @param realName 真实姓名
     * @param email 邮箱
     * @param phone 手机号
     * @return 更新后的用户对象
     */
    public User updateUserProfile(Long id, String realName, String email, String phone) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新用户信息失败：用户不存在 - ID: " + id);
            throw new BusinessException("用户不存在");
        }
        
        User user = userOptional.get();
        
        // 验证邮箱（如果提供了新邮箱）
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            ValidationUtil.validateEmail(email);
            if (userRepository.existsByEmail(email)) {
                LoggerUtil.warn(logger, "更新用户信息失败：邮箱已存在 - " + email);
                throw new BusinessException("邮箱已存在");
            }
        }
        
        // 验证手机号（如果提供了新手机号）
        if (phone != null && !phone.isEmpty() && !phone.equals(user.getPhone())) {
            ValidationUtil.validatePhone(phone);
            if (userRepository.existsByPhone(phone)) {
                LoggerUtil.warn(logger, "更新用户信息失败：手机号已存在 - " + phone);
                throw new BusinessException("手机号已存在");
            }
        }
        
        // 更新用户信息
        if (realName != null && !realName.isEmpty()) {
            user.setRealName(realName);
        }
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (phone != null && !phone.isEmpty()) {
            user.setPhone(phone);
        }
        
        User updatedUser = userRepository.save(user);
        LoggerUtil.info(logger, "更新用户信息成功 - ID: " + id);
        
        return updatedUser;
    }
    
    /**
     * 获取用户列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 用户分页数据
     */
    public Page<User> getUserList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        LoggerUtil.info(logger, "获取用户列表成功 - 页码: " + page + ", 每页数量: " + size);
        return users;
    }
    
    /**
     * 禁用用户
     * @param id 用户ID
     * @return 更新后的用户对象
     */
    public User disableUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn(logger, "禁用用户失败：用户不存在 - ID: " + id);
            throw new BusinessException("用户不存在");
        }
        
        User user = userOptional.get();
        
        // 检查是否已经禁用
        if ("disabled".equals(user.getStatus())) {
            LoggerUtil.warn(logger, "禁用用户失败：用户已被禁用 - ID: " + id);
            throw new BusinessException("用户已被禁用");
        }
        
        user.setStatus("disabled");
        User updatedUser = userRepository.save(user);
        LoggerUtil.info(logger, "禁用用户成功 - ID: " + id + ", 用户名: " + user.getUsername());
        
        return updatedUser;
    }
    
    /**
     * 启用用户
     * @param id 用户ID
     * @return 更新后的用户对象
     */
    public User enableUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn(logger, "启用用户失败：用户不存在 - ID: " + id);
            throw new BusinessException("用户不存在");
        }
        
        User user = userOptional.get();
        
        // 检查是否已经启用
        if ("active".equals(user.getStatus())) {
            LoggerUtil.warn(logger, "启用用户失败：用户已被启用 - ID: " + id);
            throw new BusinessException("用户已被启用");
        }
        
        user.setStatus("active");
        User updatedUser = userRepository.save(user);
        LoggerUtil.info(logger, "启用用户成功 - ID: " + id + ", 用户名: " + user.getUsername());
        
        return updatedUser;
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除用户失败：用户不存在 - ID: " + id);
            throw new BusinessException("用户不存在");
        }
        
        User user = userOptional.get();
        userRepository.deleteById(id);
        LoggerUtil.info(logger, "删除用户成功 - ID: " + id + ", 用户名: " + user.getUsername());
    }
}
