package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.AdminUserCreateRequest;
import com.zhuang.embroidery.dto.AdminUserUpdateRequest;
import com.zhuang.embroidery.dto.UserListResponse;
import com.zhuang.embroidery.dto.UserRegisterRequest;
import com.zhuang.embroidery.dto.UserLoginRequest;
import com.zhuang.embroidery.dto.UserResponse;
import com.zhuang.embroidery.dto.UserUpdateRequest;
import com.zhuang.embroidery.entity.User;
import com.zhuang.embroidery.repository.UserRepository;
import com.zhuang.embroidery.util.PasswordUtil;
import com.zhuang.embroidery.util.StringValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        log.info("用户注册请求: username={}", request.getUsername());

        // 验证参数
        validateRegisterRequest(request);

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            log.warn("邮箱已存在: {}", request.getEmail());
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 创建新用户
        User user = User.builder()
                .username(request.getUsername())
                .password(PasswordUtil.encodePassword(request.getPassword()))
                .email(request.getEmail())
                .role("user")
                .build();

        user = userRepository.save(user);
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());

        return UserResponse.fromUser(user);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 用户响应
     * @throws IllegalArgumentException 当用户名或密码错误时抛出
     */
    public UserResponse login(UserLoginRequest request) {
        log.info("用户登录请求: username={}", request.getUsername());

        // 验证参数
        validateLoginRequest(request);

        // 查询用户
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            log.warn("用户不存在: {}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        User user = userOptional.get();

        // 验证密码
        if (!PasswordUtil.matchPassword(request.getPassword(), user.getPassword())) {
            log.warn("密码错误: username={}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return UserResponse.fromUser(user);
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户响应
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    public UserResponse getUserInfo(Long userId) {
        log.info("获取用户信息: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId);
                    return new IllegalArgumentException("用户不存在");
                });

        return UserResponse.fromUser(user);
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 用户响应
     * @throws IllegalArgumentException 当用户不存在或邮箱已存在时抛出
     */
    @Transactional
    public UserResponse updateUserInfo(Long userId, UserUpdateRequest request) {
        log.info("更新用户信息: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId);
                    return new IllegalArgumentException("用户不存在");
                });

        // 如果更新邮箱，检查是否已被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("邮箱已存在: {}", request.getEmail());
                throw new IllegalArgumentException("邮箱已存在");
            }
            user.setEmail(request.getEmail());
        }

        // 更新其他字段
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        user = userRepository.save(user);
        log.info("用户信息更新成功: userId={}", userId);

        return UserResponse.fromUser(user);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("删除用户: userId={}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("用户不存在: userId={}", userId);
            throw new IllegalArgumentException("用户不存在");
        }

        userRepository.deleteById(userId);
        log.info("用户删除成功bbbbbb: userId={}", userId);
    }

    /**
     * 验证注册请求参数
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("确认密码不能为空");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            throw new IllegalArgumentException("用户名长度必须在3-50个字符之间");
        }

        if (request.getPassword().length() < 6 || request.getPassword().length() > 100) {
            throw new IllegalArgumentException("密码长度必须在6-100个字符之间");
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!StringValidationUtil.isValidEmail(request.getEmail())) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
        }
    }

    /**
     * 验证登录请求参数
     */
    private void validateLoginRequest(UserLoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }

    /**
     * 获取用户列表（管理员）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户列表响应
     */
    /**
     * 获取用户列表（管理员）- 支持搜索和筛选
     */
    public UserListResponse getUserList(Integer pageNum, Integer pageSize, String keyword, String role) {
        log.info("获取用户列表: pageNum={}, pageSize={}, keyword={}, role={}", pageNum, pageSize, keyword, role);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        // 👇 修改这里，调用带有条件查询的方法
        Page<User> page = userRepository.searchUsers(keyword, role, pageable);

        UserListResponse response = UserListResponse.builder()
                .users(page.getContent().stream()
                        .map(UserResponse::fromUser)
                        .collect(Collectors.toList()))
                .total(page.getTotalElements())
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();

        log.info("成功获取用户列表: total={}", page.getTotalElements());
        return response;
    }

    /**
     * 创建用户（管理员）
     *
     * @param request 创建请求
     * @return 用户响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public UserResponse createUser(AdminUserCreateRequest request) {
        log.info("创建用户: username={}", request.getUsername());

        // 验证参数
        validateAdminCreateRequest(request);

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            log.warn("邮箱已存在: {}", request.getEmail());
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 创建新用户
        User user = User.builder()
                .username(request.getUsername())
                .password(PasswordUtil.encodePassword(request.getPassword()))
                .email(request.getEmail())
                .avatar(request.getAvatar())
                .bio(request.getBio())
                .role(request.getRole() != null ? request.getRole() : "user")
                .build();

        user = userRepository.save(user);
        log.info("用户创建成功: userId={}, username={}", user.getId(), user.getUsername());

        return UserResponse.fromUser(user);
    }

    /**
     * 修改用户信息（管理员）
     *
     * @param userId 用户ID
     * @param request 修改请求
     * @return 用户响应
     * @throws IllegalArgumentException 当用户不存在或邮箱已存在时抛出
     */
    @Transactional
    public UserResponse updateUserByAdmin(Long userId, AdminUserUpdateRequest request) {
        log.info("修改用户信息（管理员）: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId);
                    return new IllegalArgumentException("用户不存在");
                });

        // 如果更新邮箱，检查是否已被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("邮箱已存在: {}", request.getEmail());
                throw new IllegalArgumentException("邮箱已存在");
            }
            user.setEmail(request.getEmail());
        }

        // 更新其他字段
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        user = userRepository.save(user);
        log.info("用户信息修改成功（管理员）: userId={}", userId);

        return UserResponse.fromUser(user);
    }

    /**
     * 验证管理员创建用户请求参数
     */
    private void validateAdminCreateRequest(AdminUserCreateRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            throw new IllegalArgumentException("用户名长度必须在3-50个字符之间");
        }

        if (request.getPassword().length() < 6 || request.getPassword().length() > 100) {
            throw new IllegalArgumentException("密码长度必须在6-100个字符之间");
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!StringValidationUtil.isValidEmail(request.getEmail())) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
        }

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            if (!request.getRole().equals("user") && !request.getRole().equals("admin")) {
                throw new IllegalArgumentException("角色只能是 user 或 admin");
            }
        }
    }

}
