package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.dto.LoginRequest;
import com.powerinspection.dto.RegisterRequest;
import com.powerinspection.entity.User;
import com.powerinspection.entity.UserStatus;
import com.powerinspection.repository.UserRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.security.JwtService;
import com.powerinspection.service.OperationLogService;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ViewService viewService;
    private final OperationLogService operationLogService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException("账号已禁用");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", jwtService.generateToken(user));
        result.put("user", viewService.userMap(user));
        operationLogService.save("认证", "登录", true, "登录成功", Map.of("username", user.getUsername()), user);
        return ApiResponse.ok(result);
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName());
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(UserStatus.ENABLED);

        User savedUser = userRepository.save(user);
        operationLogService.save("认证", "注册", true, "注册成功", Map.of("username", savedUser.getUsername()), savedUser);
        return ApiResponse.ok("注册成功", viewService.userMap(savedUser));
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me() {
        return ApiResponse.ok(viewService.userMap(AuthContext.getUser()));
    }
}
