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
    private static final Long SUPER_ADMIN_ROLE_ID = 1L;
    private static final Long ADMIN_ROLE_ID = 2L;
    private static final Long USER_ROLE_ID = 3L;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(String username, String email, String password) {
        return register(username, email, password, "user");
    }

    public User register(String username, String email, String password, String roleType) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(User.UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRoleId(resolveRegisterRoleId(roleType));

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return login(username, password, "user");
    }

    public User login(String username, String password, String roleType) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        User user = userOpt.get();

        if (user.getStatus() == User.UserStatus.DISABLED) {
            throw new IllegalArgumentException("用户已被禁用");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        validateLoginRole(user, roleType);
        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

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

    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User disableUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setStatus(User.UserStatus.DISABLED);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User enableUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setStatus(User.UserStatus.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private Long resolveRegisterRoleId(String roleType) {
        if ("admin".equalsIgnoreCase(roleType)) {
            return SUPER_ADMIN_ROLE_ID;
        }
        return USER_ROLE_ID;
    }

    private void validateLoginRole(User user, String roleType) {
        boolean adminRole = isAdminRole(user.getRoleId());

        if ("admin".equalsIgnoreCase(roleType) && !adminRole) {
            throw new IllegalArgumentException("该账号不是管理员账号");
        }

        if ("user".equalsIgnoreCase(roleType) && adminRole) {
            throw new IllegalArgumentException("请使用管理员登录");
        }
    }

    private boolean isAdminRole(Long roleId) {
        return SUPER_ADMIN_ROLE_ID.equals(roleId) || ADMIN_ROLE_ID.equals(roleId);
    }
}
