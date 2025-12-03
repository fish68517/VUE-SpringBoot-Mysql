package com.sharkfitness.controller;

import com.sharkfitness.dto.UserProfileUpdateRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.UserService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for user profile management
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get current user profile
     * @param request HTTP request containing current user ID
     * @return API response with user profile
     */
    @GetMapping("/profile")
    public ApiResponse<UserVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        UserVO userVO = userService.getProfile(userId);
        return ApiResponse.success(userVO);
    }


    @GetMapping("/profile/by-username")
    public ApiResponse<User> getProfileByName(@RequestParam("username") String username) {
        // 这里的 username 来自 URL 参数 ?username=xxx
        // 不需要 request.getAttribute("currentUserId")，因为是查别人

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        Optional<User> userVO = userRepository.findByUsername(username);
        if (userVO.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return ApiResponse.success(userVO.get());
    }
    /**
     * Update current user profile
     * @param request HTTP request containing current user ID
     * @param updateRequest profile update request
     * @return API response with updated user profile
     */
    @PutMapping("/profile")
    public ApiResponse<UserVO> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        Long userId = (Long) request.getAttribute("currentUserId");
        UserVO userVO = userService.updateProfile(userId, updateRequest);
        return ApiResponse.success(userVO);
    }
    
    /**
     * Upload user avatar
     * @param request HTTP request containing current user ID
     * @param file the avatar image file
     * @return API response with avatar URL
     */
    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {
        Long userId = (Long) request.getAttribute("currentUserId");
        String avatarUrl = userService.uploadAvatar(userId, file);
        
        Map<String, String> result = new HashMap<>();
        result.put("url", avatarUrl);
        
        return ApiResponse.success(result);
    }
}
