package com.sharkfitness.service;

import com.sharkfitness.dto.UserProfileUpdateRequest;
import com.sharkfitness.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for user operations
 */
public interface UserService {
    
    /**
     * Get user profile by user ID
     * @param userId the user ID
     * @return UserVO containing profile information
     */
    UserVO getProfile(Long userId);
    
    /**
     * Update user profile
     * @param userId the user ID
     * @param request profile update request
     * @return updated UserVO
     */
    UserVO updateProfile(Long userId, UserProfileUpdateRequest request);
    
    /**
     * Upload user avatar
     * @param userId the user ID
     * @param file the avatar image file
     * @return the URL of the uploaded avatar
     */
    String uploadAvatar(Long userId, MultipartFile file);
}
