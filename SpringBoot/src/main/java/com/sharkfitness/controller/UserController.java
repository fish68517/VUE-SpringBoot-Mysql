package com.sharkfitness.controller;

import com.sharkfitness.dto.UserProfileUpdateRequest;
import com.sharkfitness.service.UserService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for user profile management
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
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
