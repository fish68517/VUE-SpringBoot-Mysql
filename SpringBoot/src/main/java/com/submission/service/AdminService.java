package com.submission.service;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Admin Service - Business logic for admin operations
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;

    /**
     * Get admin personal information
     */
    public User getAdminInfo(Long adminId) {
        User admin = userMapper.findById(adminId);
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("User is not an admin");
        }
        return admin;
    }

    /**
     * Update admin personal information
     */
    public User updateAdminInfo(Long adminId, UserDTO userDTO) {
        User admin = userMapper.findById(adminId);
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("User is not an admin");
        }

        // Update only allowed fields for admin
        admin.setEmail(userDTO.getEmail());
        admin.setPhone(userDTO.getPhone());

        userMapper.update(admin);
        return admin;
    }

    /**
     * Update admin password
     */
    public void updateAdminPassword(Long adminId, String oldPassword, String newPassword) {
        User admin = userMapper.findById(adminId);
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("User is not an admin");
        }

        // Verify old password (plain text comparison as per requirements)
        if (!admin.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update password
        admin.setPassword(newPassword);
        userMapper.update(admin);
    }
}
