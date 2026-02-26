package com.agricultural.service;

import com.agricultural.entity.User;
import com.agricultural.repository.UserRepository;
import com.agricultural.util.EncryptionUtil;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户业务逻辑层
 * 处理用户相关的业务逻辑，包括注册、登录、查询、更新、删除等操作
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户注册
     * 验证用户信息，加密密码，创建新用户账户
     *
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param phone 手机号
     * @param userType 用户类型
     * @param region 地区
     * @return 创建的用户对象
     * @throws IllegalArgumentException 当用户信息不合法时抛出异常
     */
    public User register(String username, String password, String email, String phone,
                         User.UserType userType, String region) {
        LoggerUtil.info("开始用户注册流程，用户名: {}", username);

        // 验证用户名
        if (StringUtil.isBlank(username)) {
            LoggerUtil.warn("用户注册失败: 用户名为空");
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (username.length() < 3 || username.length() > 50) {
            LoggerUtil.warn("用户注册失败: 用户名长度不符合要求，用户名: {}", username);
            throw new IllegalArgumentException("用户名长度必须在3-50个字符之间");
        }

        // 验证密码
        if (StringUtil.isBlank(password)) {
            LoggerUtil.warn("用户注册失败: 密码为空");
            throw new IllegalArgumentException("密码不能为空");
        }

        if (password.length() < 6 || password.length() > 50) {
            LoggerUtil.warn("用户注册失败: 密码长度不符合要求");
            throw new IllegalArgumentException("密码长度必须在6-50个字符之间");
        }

        // 验证邮箱
        if (StringUtil.isNotBlank(email) && !StringUtil.isValidEmail(email)) {
            LoggerUtil.warn("用户注册失败: 邮箱格式不正确，邮箱: {}", email);
            throw new IllegalArgumentException("邮箱格式不正确");
        }

        // 验证手机号
 /*       if (StringUtil.isNotBlank(phone) && !StringUtil.isValidPhone(phone)) {
            LoggerUtil.warn("用户注册失败: 手机号格式不正确，手机号: {}", phone);
            throw new IllegalArgumentException("手机号格式不正确");
        }*/

        // 验证用户类型
        if (userType == null) {
            LoggerUtil.warn("用户注册失败: 用户类型为空");
            throw new IllegalArgumentException("用户类型不能为空");
        }

        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            LoggerUtil.warn("用户注册失败: 用户名已存在，用户名: {}", username);
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (StringUtil.isNotBlank(email)) {
            Optional<User> existingEmail = userRepository.findByEmail(email);
            if (existingEmail.isPresent()) {
                LoggerUtil.warn("用户注册失败: 邮箱已存在，邮箱: {}", email);
                throw new IllegalArgumentException("邮箱已存在");
            }
        }

        // 创建新用户
        User user = User.builder()
                .username(username)
                .password(EncryptionUtil.encryptPassword(password))
                .email(email)
                .phone(phone)
                .userType(userType)
                .region(region)
                .status(User.UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        LoggerUtil.info("用户注册成功，用户ID: {}, 用户名: {}", savedUser.getId(), username);

        return savedUser;
    }

    /**
     * 用户登录
     * 验证用户身份和密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象
     * @throws IllegalArgumentException 当用户名或密码错误时抛出异常
     */
    public User login(String username, String password) {
        LoggerUtil.info("开始用户登录流程，用户名: {}", username);

        // 验证用户名和密码不为空
        if (StringUtil.isBlank(username) || StringUtil.isBlank(password)) {
            LoggerUtil.warn("用户登录失败: 用户名或密码为空");
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        // 根据用户名查询用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn("用户登录失败: 用户不存在，用户名: {}", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        User user = userOptional.get();

        // 检查用户状态
        if (user.getStatus() == User.UserStatus.LOCKED) {
            LoggerUtil.warn("用户登录失败: 用户账户已锁定，用户ID: {}", user.getId());
            throw new IllegalArgumentException("用户账户已锁定，请联系管理员");
        }

        if (user.getStatus() == User.UserStatus.INACTIVE) {
            LoggerUtil.warn("用户登录失败: 用户账户已禁用，用户ID: {}", user.getId());
            throw new IllegalArgumentException("用户账户已禁用");
        }

        // 验证密码
        if (!EncryptionUtil.verifyPassword(password, user.getPassword())) {
            LoggerUtil.warn("用户登录失败: 密码错误，用户ID: {}", user.getId());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        LoggerUtil.info("用户登录成功，用户ID: {}, 用户名: {}", user.getId(), username);

        return user;
    }

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象
     * @throws IllegalArgumentException 当用户不存在时抛出异常
     */
    public User getUserById(Long userId) {
        LoggerUtil.info("查询用户信息，用户ID: {}", userId);

        if (userId == null || userId <= 0) {
            LoggerUtil.warn("查询用户失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn("查询用户失败: 用户不存在，用户ID: {}", userId);
            throw new IllegalArgumentException("用户不存在");
        }

        LoggerUtil.info("查询用户成功，用户ID: {}", userId);

        return userOptional.get();
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     * @throws IllegalArgumentException 当用户不存在时抛出异常
     */
    public User getUserByUsername(String username) {
        LoggerUtil.info("根据用户名查询用户，用户名: {}", username);

        if (StringUtil.isBlank(username)) {
            LoggerUtil.warn("查询用户失败: 用户名为空");
            throw new IllegalArgumentException("用户名不能为空");
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn("查询用户失败: 用户不存在，用户名: {}", username);
            throw new IllegalArgumentException("用户不存在");
        }

        LoggerUtil.info("根据用户名查询用户成功，用户名: {}", username);

        return userOptional.get();
    }

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        LoggerUtil.info("查询所有用户");

        List<User> users = userRepository.findAll();
        LoggerUtil.info("查询所有用户成功，用户总数: {}", users.size());

        return users;
    }



    /**
     * 根据地区查询用户列表
     *
     * @param region 地区
     * @return 用户列表
     */
    public List<User> getUsersByRegion(String region) {
        LoggerUtil.info("根据地区查询用户，地区: {}", region);

        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("查询用户失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        List<User> users = userRepository.findByRegion(region);
        LoggerUtil.info("根据地区查询用户成功，地区: {}, 用户数: {}", region, users.size());

        return users;
    }

    /**
     * 根据用户状态查询用户列表
     *
     * @param status 用户状态
     * @return 用户列表
     */
    public List<User> getUsersByStatus(User.UserStatus status) {
        LoggerUtil.info("根据用户状态查询用户，用户状态: {}", status);

        if (status == null) {
            LoggerUtil.warn("查询用户失败: 用户状态为空");
            throw new IllegalArgumentException("用户状态不能为空");
        }

        List<User> users = userRepository.findByStatus(status);
        LoggerUtil.info("根据用户状态查询用户成功，用户状态: {}, 用户数: {}", status, users.size());

        return users;
    }

    /**
     * 更新用户信息
     * 支持更新邮箱、手机号、地区等信息
     *
     * @param userId 用户ID
     * @param email 邮箱
     * @param phone 手机号
     * @param region 地区
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 当用户不存在或信息不合法时抛出异常
     */
    public User updateUser(Long userId, String email, String phone, String region) {
        LoggerUtil.info("开始更新用户信息，用户ID: {}", userId);

        // 获取用户
        User user = getUserById(userId);

        // 验证邮箱
        if (StringUtil.isNotBlank(email)) {
            if (!StringUtil.isValidEmail(email)) {
                LoggerUtil.warn("更新用户失败: 邮箱格式不正确，用户ID: {}", userId);
                throw new IllegalArgumentException("邮箱格式不正确");
            }

            // 检查邮箱是否已被其他用户使用
            Optional<User> existingEmail = userRepository.findByEmail(email);
            if (existingEmail.isPresent() && !existingEmail.get().getId().equals(userId)) {
                LoggerUtil.warn("更新用户失败: 邮箱已被其他用户使用，用户ID: {}", userId);
                throw new IllegalArgumentException("邮箱已被其他用户使用");
            }

            user.setEmail(email);
        }

        // 验证手机号
        if (StringUtil.isNotBlank(phone)) {
            if (!StringUtil.isValidPhone(phone)) {
                LoggerUtil.warn("更新用户失败: 手机号格式不正确，用户ID: {}", userId);
                throw new IllegalArgumentException("手机号格式不正确");
            }

            user.setPhone(phone);
        }

        // 更新地区
        if (StringUtil.isNotBlank(region)) {
            user.setRegion(region);
        }

        User updatedUser = userRepository.save(user);
        LoggerUtil.info("用户信息更新成功，用户ID: {}", userId);

        return updatedUser;
    }

    /**
     * 修改用户密码
     * 验证旧密码，然后更新为新密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 当旧密码错误或新密码不合法时抛出异常
     */
    public User changePassword(Long userId, String oldPassword, String newPassword) {
        LoggerUtil.info("开始修改用户密码，用户ID: {}", userId);

        // 获取用户
        User user = getUserById(userId);

        // 验证旧密码
        if (!EncryptionUtil.verifyPassword(oldPassword, user.getPassword())) {
            LoggerUtil.warn("修改密码失败: 旧密码错误，用户ID: {}", userId);
            throw new IllegalArgumentException("旧密码错误");
        }

        // 验证新密码
        if (StringUtil.isBlank(newPassword)) {
            LoggerUtil.warn("修改密码失败: 新密码为空，用户ID: {}", userId);
            throw new IllegalArgumentException("新密码不能为空");
        }

        if (newPassword.length() < 6 || newPassword.length() > 50) {
            LoggerUtil.warn("修改密码失败: 新密码长度不符合要求，用户ID: {}", userId);
            throw new IllegalArgumentException("新密码长度必须在6-50个字符之间");
        }

        // 检查新密码是否与旧密码相同
        if (oldPassword.equals(newPassword)) {
            LoggerUtil.warn("修改密码失败: 新密码与旧密码相同，用户ID: {}", userId);
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }

        // 更新密码
        user.setPassword(EncryptionUtil.encryptPassword(newPassword));
        User updatedUser = userRepository.save(user);

        LoggerUtil.info("用户密码修改成功，用户ID: {}", userId);

        return updatedUser;
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 新状态
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 当用户不存在或状态无效时抛出异常
     */
    public User updateUserStatus(Long userId, User.UserStatus status) {
        LoggerUtil.info("开始更新用户状态，用户ID: {}, 新状态: {}", userId, status);

        // 获取用户
        User user = getUserById(userId);

        // 验证状态
        if (status == null) {
            LoggerUtil.warn("更新用户状态失败: 状态为空，用户ID: {}", userId);
            throw new IllegalArgumentException("用户状态不能为空");
        }

        user.setStatus(status);
        User updatedUser = userRepository.save(user);

        LoggerUtil.info("用户状态更新成功，用户ID: {}, 新状态: {}", userId, status);

        return updatedUser;
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @throws IllegalArgumentException 当用户不存在时抛出异常
     */
    public void deleteUser(Long userId) {
        LoggerUtil.info("开始删除用户，用户ID: {}", userId);

        // 验证用户是否存在
        User user = getUserById(userId);

        userRepository.deleteById(userId);
        LoggerUtil.info("用户删除成功，用户ID: {}", userId);
    }

    /**
     * 验证用户身份
     * 用于验证用户是否存在且状态正常
     *
     * @param userId 用户ID
     * @return 如果用户存在且状态为活跃，返回true；否则返回false
     */
    public boolean validateUser(Long userId) {
        LoggerUtil.info("验证用户身份，用户ID: {}", userId);

        if (userId == null || userId <= 0) {
            LoggerUtil.warn("用户验证失败: 用户ID无效");
            return false;
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            LoggerUtil.warn("用户验证失败: 用户不存在，用户ID: {}", userId);
            return false;
        }

        User user = userOptional.get();
        boolean isValid = user.getStatus() == User.UserStatus.ACTIVE;

        if (isValid) {
            LoggerUtil.info("用户验证成功，用户ID: {}", userId);
        } else {
            LoggerUtil.warn("用户验证失败: 用户状态不正常，用户ID: {}, 状态: {}", userId, user.getStatus());
        }

        return isValid;
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 如果用户名存在，返回true；否则返回false
     */
    public boolean usernameExists(String username) {
        LoggerUtil.info("检查用户名是否存在，用户名: {}", username);

        if (StringUtil.isBlank(username)) {
            return false;
        }

        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 如果邮箱存在，返回true；否则返回false
     */
    public boolean emailExists(String email) {
        LoggerUtil.info("检查邮箱是否存在，邮箱: {}", email);

        if (StringUtil.isBlank(email)) {
            return false;
        }

        return userRepository.findByEmail(email).isPresent();
    }
}
