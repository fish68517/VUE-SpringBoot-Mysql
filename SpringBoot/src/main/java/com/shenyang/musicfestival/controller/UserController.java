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
import java.util.Locale;
import java.util.UUID;

/**
 * Controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${file.upload.max-size}")
    private long maxFileSize;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegisterRequest request) {
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("手机号不能为空"));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码不能为空"));
        }

        if (userService.phoneExists(request.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("该手机号已被注册"));
        }

        User user = userService.register(request.getPhone(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(convertToDTO(user), "注册成功"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("手机号不能为空"));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码不能为空"));
        }

        var user = userService.login(request.getPhone(), request.getPassword());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("手机号或密码错误"));
        }

        User authenticatedUser = user.get();
        if (Boolean.TRUE.equals(authenticatedUser.getIsBlocked())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("账户已被封禁"));
        }

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
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "userId", required = false) Long requestUserId) {
        Long userId = resolveUserId(token, requestUserId);
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(ApiResponse.success(convertToDTO(user), "获取个人信息成功")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("用户不存在")));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "userId", required = false) Long requestUserId,
            @RequestBody UserDTO userDTO) {
        Long userId = resolveUserId(token, requestUserId);
        User updatedUser = userService.updateProfile(userId, userDTO);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updatedUser), "个人信息更新成功"));
    }

    @PostMapping("/real-name")
    public ResponseEntity<ApiResponse<UserDTO>> verifyRealName(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "userId", required = false) Long requestUserId,
            @RequestBody RealNameRequest request) {
        Long userId = resolveUserId(token, requestUserId);

        if (request.getRealName() == null || request.getRealName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("真实姓名不能为空"));
        }
        if (request.getIdNumber() == null || request.getIdNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("身份证号不能为空"));
        }

        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("用户不存在"));
        }

        if (userService.idNumberExists(request.getIdNumber())) {
            User existingUser = userService.findByIdNumber(request.getIdNumber()).get();
            if (!existingUser.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("该身份证号已被使用"));
            }
        }

        User verifiedUser = userService.verifyRealName(userId, request.getRealName(), request.getIdNumber());
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(verifiedUser), "实名认证成功"));
    }

    @GetMapping("/real-name")
    public ResponseEntity<ApiResponse<RealNameStatusResponse>> getRealNameStatus(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "userId", required = false) Long requestUserId) {
        Long userId = resolveUserId(token, requestUserId);
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("用户不存在"));
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
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "userId", required = false) Long requestUserId,
            @RequestParam("file") MultipartFile file) {
        Long userId = resolveUserId(token, requestUserId);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("文件不能为空"));
        }
        if (file.getSize() > maxFileSize) {
            return ResponseEntity.badRequest().body(ApiResponse.error("文件大小超过限制（最大10MB）"));
        }

        String extension = getImageExtension(file.getOriginalFilename());
        if (extension == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("仅支持 jpg、jpeg、png、gif、webp 格式图片"));
        }

        try {
            File imageDir = new File(System.getProperty("user.dir"), "images");
            if (!imageDir.exists() && !imageDir.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("图片目录创建失败"));
            }

            String uniqueFilename = "avatar_" + UUID.randomUUID() + extension;
            File dest = new File(imageDir, uniqueFilename);
            file.transferTo(dest);

            User updatedUser = userService.uploadAvatar(userId, uniqueFilename);
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(updatedUser), "头像上传成功"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("文件上传失败"));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "userId", required = false) Long requestUserId,
            @RequestBody ChangePasswordRequest request) {
        Long userId = resolveUserId(token, requestUserId);

        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("原密码不能为空"));
        }
        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            return ResponseEntity.badRequest().body(ApiResponse.error("新密码长度不能少于6位"));
        }

        try {
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success(null, "密码修改成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    private Long resolveUserId(String token, Long requestUserId) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                return jwtUtil.extractUserId(token.substring(7));
            }
            if (token != null && !token.trim().isEmpty()) {
                return jwtUtil.extractUserId(token);
            }
        } catch (Exception ignored) {
        }
        return requestUserId == null ? 1L : requestUserId;
    }

    private String getImageExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return null;
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(Locale.ROOT);
        if (!extension.equals(".jpg")
                && !extension.equals(".jpeg")
                && !extension.equals(".png")
                && !extension.equals(".gif")
                && !extension.equals(".webp")) {
            return null;
        }
        return extension;
    }

    private UserDTO convertToDTO(User user) {
        String maskedIdNumber = user.getIdNumber() != null ? MaskingUtil.maskIdNumber(user.getIdNumber()) : null;

        return UserDTO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .contactPhone(user.getContactPhone())
                .shippingAddress(user.getShippingAddress())
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
