package com.medical.internship.service.impl;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.UserRegisterRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.dto.UserUpdateRequest;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.User;
import com.medical.internship.repository.OrganizationRepository;
import com.medical.internship.repository.UserRepository;
import com.medical.internship.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Override
    public UserResponse register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 获取组织信息
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在"));
        
        // 创建用户
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(request.getRole())
                .organization(organization)
                .status("PENDING")
                .build();
        
        user = userRepository.save(user);
        log.info("用户注册成功: {}", user.getUsername());
        
        return convertToResponse(user);
    }
    
    @Override
    public UserResponse login(String username, String password) {
        // 查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        
        // 验证密码（简化实现，实际应使用加密）
        if (!user.getPassword().equals(password)) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查用户状态
        if ("REJECTED".equals(user.getStatus())) {
            throw new BusinessException("用户已被驳回，无法登录");
        }
        
        log.info("用户登录成功: {}", username);
        return convertToResponse(user);
    }
    
    @Override
    public UserResponse getUserProfile(Long userId) {
        User user = getUserById(userId);
        return convertToResponse(user);
    }
    
    @Override
    public UserResponse updateUserProfile(Long userId, UserUpdateRequest request) {
        User user = getUserById(userId);
        
        // 检查邮箱是否已被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }
        
        user = userRepository.save(user);
        log.info("用户信息更新成功: {}", userId);
        
        return convertToResponse(user);
    }
    
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
    
    @Override
    public java.util.List<UserResponse> getPendingStudents(Long schoolId) {
        // 获取该学校所有待认证的学生
        java.util.List<User> students = userRepository.findByOrganizationIdAndRole(schoolId, "STUDENT");
        return students.stream()
                .filter(s -> "PENDING".equals(s.getStatus()))
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public UserResponse approveStudent(Long userId, Long schoolId) {
        User user = getUserById(userId);
        
        // 验证用户属于该学校
        if (!user.getOrganization().getId().equals(schoolId)) {
            throw new BusinessException("无权操作该用户");
        }
        
        user.setStatus("APPROVED");
        user = userRepository.save(user);
        log.info("学生认证通过: {}", userId);
        
        return convertToResponse(user);
    }
    
    @Override
    public UserResponse rejectStudent(Long userId, Long schoolId) {
        User user = getUserById(userId);
        
        // 验证用户属于该学校
        if (!user.getOrganization().getId().equals(schoolId)) {
            throw new BusinessException("无权操作该用户");
        }
        
        user.setStatus("REJECTED");
        user = userRepository.save(user);
        log.info("学生认证驳回: {}", userId);
        
        return convertToResponse(user);
    }
    
    @Override
    public java.util.List<UserResponse> getPendingUsers() {
        // 获取所有待认证用户（不包括系统管理员）
        java.util.List<User> users = userRepository.findAll();
        return users.stream()
                .filter(u -> "PENDING".equals(u.getStatus()) && !"ADMIN".equals(u.getRole()))
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public UserResponse approveUserAuth(Long userId) {
        User user = getUserById(userId);
        user.setStatus("APPROVED");
        user = userRepository.save(user);
        log.info("用户认证通过: {}", userId);
        return convertToResponse(user);
    }
    
    @Override
    public UserResponse rejectUserAuth(Long userId) {
        User user = getUserById(userId);
        user.setStatus("REJECTED");
        user = userRepository.save(user);
        log.info("用户认证驳回: {}", userId);
        return convertToResponse(user);
    }
    
    /**
     * 将User实体转换为UserResponse DTO
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .organization(convertOrganizationToResponse(user.getOrganization()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * 将Organization实体转换为OrganizationResponse DTO
     */
    private OrganizationResponse convertOrganizationToResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .type(organization.getType())
                .code(organization.getCode())
                .build();
    }
}
