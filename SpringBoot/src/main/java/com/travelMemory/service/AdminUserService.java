package com.travelMemory.service;

import com.travelMemory.dto.AdminUserCreateRequest;
import com.travelMemory.dto.AdminUserUpdateRequest;
import com.travelMemory.dto.UserResponse;
import com.travelMemory.entity.User;
import com.travelMemory.entity.UserRole;
import com.travelMemory.repository.UserRepository;
import com.travelMemory.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        return toResponse(findUser(userId));
    }

    public UserResponse createUser(AdminUserCreateRequest request) {
        String username = SecurityUtils.sanitizeUsername(request.getUsername());
        String email = SecurityUtils.sanitizeEmail(request.getEmail());

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .avatarUrl(cleanOptionalText(request.getAvatarUrl()))
                .bio(cleanOptionalText(request.getBio()))
                .role(parseRole(request.getRole()))
                .build();

        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long userId, AdminUserUpdateRequest request) {
        User user = findUser(userId);

        if (StringUtils.hasText(request.getUsername())) {
            String username = SecurityUtils.sanitizeUsername(request.getUsername());
            if (userRepository.existsByUsernameAndIdNot(username, userId)) {
                throw new IllegalArgumentException("用户名已存在");
            }
            user.setUsername(username);
        }

        if (StringUtils.hasText(request.getEmail())) {
            String email = SecurityUtils.sanitizeEmail(request.getEmail());
            if (userRepository.existsByEmailAndIdNot(email, userId)) {
                throw new IllegalArgumentException("邮箱已存在");
            }
            user.setEmail(email);
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(cleanOptionalText(request.getAvatarUrl()));
        }
        if (request.getBio() != null) {
            user.setBio(cleanOptionalText(request.getBio()));
        }
        if (StringUtils.hasText(request.getRole())) {
            user.setRole(parseRole(request.getRole()));
        }

        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        User user = findUser(userId);
        userRepository.delete(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("角色只能是 USER 或 ADMIN");
        }
    }

    private String cleanOptionalText(String value) {
        return StringUtils.hasText(value) ? SecurityUtils.sanitizeText(value.trim()) : null;
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
