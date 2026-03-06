package com.submission.service;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * User Service - Business logic for user operations
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * Register a new user
     */
    public User register(UserDTO userDTO) {
        // Check if username already exists
        User existingUser = userMapper.findByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }

        // Create new user with PENDING status
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
     * Login user with username and password
     */
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Validate password (plain text comparison as per requirements)
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // Check if user is approved
        if (!"ACTIVE".equals(user.getStatus()) && !"APPROVED".equals(user.getStatus())) {
            throw new RuntimeException("User account is not approved");
        }

        return user;
    }

    /**
     * Get user by id
     */
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * Update user information
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
     * Get users by role
     */
    public java.util.List<User> getUsersByRole(String role) {
        return userMapper.findByRole(role);
    }
}
