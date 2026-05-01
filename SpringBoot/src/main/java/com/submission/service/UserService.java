package com.submission.service;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户服务：处理注册、登录和用户基础信息维护。
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * 注册新用户。注册后先进入待审核状态，由管理员审核通过后启用。
     */
    public User register(UserDTO userDTO) {
        User existingUser = userMapper.findByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .role(userDTO.getRole())
                .status("PENDING")
                .academicAchievements(userDTO.getAcademicAchievements())
                .workEmail(userDTO.getWorkEmail())
                .expertiseAreas(userDTO.getExpertiseAreas())
                .researchDirections(userDTO.getResearchDirections())
                .build();

        userMapper.insert(user);

        return user;
    }

    /**
     * 使用用户名和明文密码登录。
     */
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        if (!"ACTIVE".equals(user.getStatus()) && !"APPROVED".equals(user.getStatus())) {
            throw new RuntimeException("账号尚未审核通过，暂不能登录");
        }

        return user;
    }

    /**
     * 根据 ID 查询用户。
     */
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 根据用户名查询用户。
     */
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 更新用户信息。
     */
    public User updateUser(UserDTO userDTO) {
        User user = User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .role(userDTO.getRole())
                .status(userDTO.getStatus())
                .academicAchievements(userDTO.getAcademicAchievements())
                .workEmail(userDTO.getWorkEmail())
                .expertiseAreas(userDTO.getExpertiseAreas())
                .researchDirections(userDTO.getResearchDirections())
                .build();

        userMapper.update(user);
        return user;
    }

    /**
     * 根据角色查询已启用用户。
     */
    public java.util.List<User> getUsersByRole(String role) {
        return userMapper.findByRole(role);
    }
}
