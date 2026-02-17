package com.naxi.service;

import com.naxi.entity.User;
import com.naxi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     * 验证用户名、邮箱唯一性，密码加密存储
     */
    public User register(String username, String email, String password) {
        // 验证用户名唯一性
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 验证邮箱唯一性
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(User.UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

      user.setRoleId(1L);

        return userRepository.save(user);
    }

    /**
     * 用户登录
     * 验证用户名和密码
     */
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        User user = userOpt.get();

        // 检查用户状态
        if (user.getStatus() == User.UserStatus.DISABLED) {
            throw new IllegalArgumentException("用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        return user;
    }

    /**
     * 获取用户信息
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * 根据用户名获取用户
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱获取用户
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 更新用户信息
     */
    public User updateUserInfo(Long id, String nickname, String avatarUrl, String bio) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        if (bio != null) {
            user.setBio(bio);
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 禁用用户（管理员操作）
     */
    public User disableUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setStatus(User.UserStatus.DISABLED);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 启用用户（管理员操作）
     */
    public User enableUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setStatus(User.UserStatus.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 保存用户
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
