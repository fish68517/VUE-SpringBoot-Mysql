package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.*;
import com.shenyang.musicfestival.entity.User;
import com.shenyang.musicfestival.service.UserService;
import com.shenyang.musicfestival.util.ApiResponse;
import com.shenyang.musicfestival.util.JwtUtil;
import com.shenyang.musicfestival.util.MaskingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Controller for User operations
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.max-size}")
    private long maxFileSize;

    @Value("${file.upload.allowed-extensions}")
    private String allowedExtensions;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegisterRequest request) {
        // Validate input
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("手机号不能为空"));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("密码不能为空"));
        }

        // Check if phone already exists
        if (userService.phoneExists(request.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("该手机号已被注册"));
        }

        // Register user
        User user = userService.register(request.getPhone(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(convertToDTO(user), "注册成功"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        // Validate input
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("手机号不能为空"));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("密码不能为空"));
        }

        // Authenticate user
        var user = userService.login(request.getPhone(), request.getPassword());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("手机号或密码错误"));
        }

        User authenticatedUser = user.get();

        // Check if user is blocked
        if (authenticatedUser.getIsBlocked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("账户已被封禁"));
        }

        // Generate tokens
        String token = jwtUtil.generateToken(authenticatedUser.getId(), authenticatedUser.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(authenticatedUser.getId());

        LoginResponse response = LoginResponse.builder()
            .userId(authenticatedUser.getId())
            .phone(authenticatedUser.getPhone())
            .token(token)
            .refreshToken(refreshToken)
            .role(authenticatedUser.getRole())
            .build();

        return ResponseEntity.ok(ApiResponse.success(response, "登录成功"));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@RequestParam Long userId) {
        return userService.getUserById(userId)
            .map(user -> ResponseEntity.ok(convertToDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestParam Long userId, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateProfile(userId, userDTO);
        return ResponseEntity.ok(convertToDTO(updatedUser));
    }

    @PostMapping("/real-name")
    public ResponseEntity<ApiResponse<UserDTO>> verifyRealName(
        @RequestParam Long userId,
        @RequestBody RealNameRequest request) {
        
        // Validate input
        if (request.getRealName() == null || request.getRealName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("真实姓名不能为空"));
        }
        if (request.getIdNumber() == null || request.getIdNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("身份证号不能为空"));
        }

        // Check if user exists
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("用户不存在"));
        }

        // Check if ID number already exists (prevent duplicate)
        if (userService.idNumberExists(request.getIdNumber())) {
            User existingUser = userService.findByIdNumber(request.getIdNumber()).get();
            if (!existingUser.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("该身份证号已被使用"));
            }
        }

        // Verify real name
        User verifiedUser = userService.verifyRealName(userId, request.getRealName(), request.getIdNumber());
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(verifiedUser), "实名认证成功"));
    }

    @GetMapping("/real-name")
    public ResponseEntity<ApiResponse<RealNameStatusResponse>> getRealNameStatus(@RequestParam Long userId) {
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("用户不存在"));
        }

        RealNameStatusResponse response = RealNameStatusResponse.builder()
            .isVerified(user.get().getIsRealNameVerified())
            .realName(user.get().getRealName())
            .maskedIdNumber(user.get().getIdNumber() != null ? MaskingUtil.maskIdNumber(user.get().getIdNumber()) : null)
            .build();

        return ResponseEntity.ok(ApiResponse.success(response, "获取实名认证状态成功"));
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<UserDTO>> uploadAvatar(
        @RequestParam Long userId,
        @RequestParam("file") MultipartFile file) {
        
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("文件不能为空"));
        }

        if (file.getSize() > maxFileSize) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("文件大小超过限制（最大10MB）"));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("文件名无效"));
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedExtensions.contains(fileExtension)) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("不支持的文件类型"));
        }

        try {
            // Create upload directory if not exists
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID() + "." + fileExtension;
            Path filePath = Paths.get(uploadPath, uniqueFilename);

            // Save file
            Files.write(filePath, file.getBytes());

            // Update user avatar
            String avatarUrl = "/uploads/" + uniqueFilename;
            User updatedUser = userService.uploadAvatar(userId, avatarUrl);

            return ResponseEntity.ok(ApiResponse.success(convertToDTO(updatedUser), "头像上传成功"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("文件上传失败"));
        }
    }

    private UserDTO convertToDTO(User user) {
        String maskedIdNumber = user.getIdNumber() != null ? MaskingUtil.maskIdNumber(user.getIdNumber()) : null;
        
        return UserDTO.builder()
            .id(user.getId())
            .phone(user.getPhone())
            .nickname(user.getNickname())
            .avatar(user.getAvatar())
            .email(user.getEmail())
            .realName(user.getRealName())
            .idNumber(user.getIdNumber())
            .maskedIdNumber(maskedIdNumber)
            .isRealNameVerified(user.getIsRealNameVerified())
            .points(user.getPoints())
            .isBlocked(user.getIsBlocked())
            .role(user.getRole())
            .build();
    }

}
