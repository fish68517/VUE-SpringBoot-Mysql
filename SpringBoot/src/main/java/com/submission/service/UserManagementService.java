package com.submission.service;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Management Service - Business logic for admin user management operations
 */
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserMapper userMapper;
    private final NotificationService notificationService;

    /**
     * Get pending users awaiting approval
     */
    public List<UserDTO> getPendingUsers() {
        List<User> users = userMapper.findPendingUsers();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Approve user registration
     */
    public UserDTO approveUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!"PENDING".equals(user.getStatus())) {
            throw new RuntimeException("User is not in pending status");
        }

        userMapper.updateStatus(userId, "APPROVED");
        user.setStatus("APPROVED");
        
        // Send approval notification
        notificationService.sendUserApprovalNotification(userId, true);
        
        return convertToDTO(user);
    }

    /**
     * Reject user registration
     */
    public UserDTO rejectUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!"PENDING".equals(user.getStatus())) {
            throw new RuntimeException("User is not in pending status");
        }

        userMapper.updateStatus(userId, "REJECTED");
        user.setStatus("REJECTED");
        
        // Send rejection notification
        notificationService.sendUserApprovalNotification(userId, false);
        
        return convertToDTO(user);
    }

    /**
     * Get all users with optional filtering
     */
    public List<UserDTO> getAllUsers(String username, String email, String role, String status) {
        List<User> users = userMapper.findAllUsers(username, email, role, status);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update user information
     */
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Update allowed fields
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(userDTO.getPassword());
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        if (userDTO.getAcademicAchievements() != null) {
            user.setAcademicAchievements(userDTO.getAcademicAchievements());
        }
        if (userDTO.getWorkEmail() != null) {
            user.setWorkEmail(userDTO.getWorkEmail());
        }
        if (userDTO.getExpertiseAreas() != null) {
            user.setExpertiseAreas(userDTO.getExpertiseAreas());
        }
        if (userDTO.getResearchDirections() != null) {
            user.setResearchDirections(userDTO.getResearchDirections());
        }

        userMapper.update(user);
        return convertToDTO(user);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        userMapper.deleteById(userId);
    }

    /**
     * Assign user role/permission
     */
    public UserDTO assignUserRole(Long userId, String role) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setRole(role);
        userMapper.update(user);
        return convertToDTO(user);
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .academicAchievements(user.getAcademicAchievements())
                .workEmail(user.getWorkEmail())
                .expertiseAreas(user.getExpertiseAreas())
                .researchDirections(user.getResearchDirections())
                .build();
    }
}
