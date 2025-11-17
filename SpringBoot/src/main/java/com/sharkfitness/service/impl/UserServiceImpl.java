package com.sharkfitness.service.impl;

import com.sharkfitness.dto.UserProfileUpdateRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.FileUploadService;
import com.sharkfitness.service.UserService;
import com.sharkfitness.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of user service
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    
    @Override
    public UserVO getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return convertToVO(user);
    }
    
    @Override
    @Transactional
    public UserVO updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update fields if provided
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getIntro() != null) {
            user.setIntro(request.getIntro());
        }
        
        user = userRepository.save(user);
        
        return convertToVO(user);
    }
    
    @Override
    @Transactional
    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Upload avatar file
        String avatarUrl = fileUploadService.uploadAvatar(file);
        
        // Update user avatar
        user.setAvatar(avatarUrl);
        userRepository.save(user);
        
        return avatarUrl;
    }
    
    /**
     * Convert User entity to UserVO
     */
    private UserVO convertToVO(User user) {
        return new UserVO(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getAvatar(),
            user.getGender(),
            user.getIntro(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
