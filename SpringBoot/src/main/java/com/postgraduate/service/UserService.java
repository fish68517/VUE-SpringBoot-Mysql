package com.postgraduate.service;

import com.postgraduate.dto.UserProfileDTO;
import com.postgraduate.entity.User;
import com.postgraduate.entity.UserProfile;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.UserProfileRepository;
import com.postgraduate.repository.UserRepository;
import com.postgraduate.util.AuthorizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for handling user profile management operations.
 * Manages user profile retrieval and updates with validation.
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    /**
     * Retrieve user profile for the current authenticated user.
     *
     * @return UserProfileDTO containing the user's profile information
     * @throws ResourceNotFoundException if user or profile is not found
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile() {
        log.info("Retrieving user profile for current user");
        User user = authorizationUtil.getCurrentUser();
        
        UserProfile userProfile = userProfileRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        
        return UserProfileDTO.fromEntity(userProfile);
    }

    /**
     * Update user profile for the current authenticated user.
     * Validates all profile fields before updating.
     *
     * @param profileDTO the updated profile information
     * @return UserProfileDTO containing the updated profile
     * @throws ResourceNotFoundException if user or profile is not found
     * @throws ValidationException if profile data is invalid
     */
    @Transactional
    public UserProfileDTO updateUserProfile(UserProfileDTO profileDTO) {
        log.info("Updating user profile for current user");
        User user = authorizationUtil.getCurrentUser();
        
        // Validate profile fields
        validateProfileFields(profileDTO);
        
        UserProfile userProfile = userProfileRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        
        // Update profile fields
        if (profileDTO.getUndergradTier() != null) {
            userProfile.setUndergradTier(profileDTO.getUndergradTier());
        }
        if (profileDTO.getGpa() != null) {
            userProfile.setGpa(profileDTO.getGpa());
        }
        if (profileDTO.getGpaScale() != null) {
            userProfile.setGpaScale(profileDTO.getGpaScale());
        }
        if (profileDTO.getCet4Score() != null) {
            userProfile.setCet4Score(profileDTO.getCet4Score());
        }
        if (profileDTO.getCet6Score() != null) {
            userProfile.setCet6Score(profileDTO.getCet6Score());
        }
        if (profileDTO.getTargetScore() != null) {
            userProfile.setTargetScore(profileDTO.getTargetScore());
        }
        if (profileDTO.getOtherScores() != null) {
            userProfile.setOtherScores(profileDTO.getOtherScores());
        }
        
        userProfile = userProfileRepository.save(userProfile);
        log.info("User profile updated successfully for user id: {}", user.getId());
        
        return UserProfileDTO.fromEntity(userProfile);
    }

    /**
     * Validate profile fields for correctness and acceptable ranges.
     *
     * @param profileDTO the profile data to validate
     * @throws ValidationException if any field is invalid
     */
    private void validateProfileFields(UserProfileDTO profileDTO) {
        // Validate GPA range (0-4.0 for most scales, 0-5.0 for some)
        if (profileDTO.getGpa() != null) {
            if (profileDTO.getGpa().compareTo(BigDecimal.ZERO) < 0 || 
                profileDTO.getGpa().compareTo(new BigDecimal("5.0")) > 0) {
                throw new ValidationException("GPA must be between 0 and 5.0");
            }
        }
        
        // Validate CET-4 score range (0-710)
        if (profileDTO.getCet4Score() != null) {
            if (profileDTO.getCet4Score() < 0 || profileDTO.getCet4Score() > 710) {
                throw new ValidationException("CET-4 score must be between 0 and 710");
            }
        }
        
        // Validate CET-6 score range (0-710)
        if (profileDTO.getCet6Score() != null) {
            if (profileDTO.getCet6Score() < 0 || profileDTO.getCet6Score() > 710) {
                throw new ValidationException("CET-6 score must be between 0 and 710");
            }
        }
        
        // Validate target score range (0-500 for postgraduate entrance exam)
        if (profileDTO.getTargetScore() != null) {
            if (profileDTO.getTargetScore() < 0 || profileDTO.getTargetScore() > 500) {
                throw new ValidationException("Target score must be between 0 and 500");
            }
        }
        
        // Validate undergrad tier
        if (profileDTO.getUndergradTier() != null) {
            String tier = profileDTO.getUndergradTier();
            if (!tier.matches("^(985|211|DOUBLE_NON|OTHER)$")) {
                throw new ValidationException("Invalid undergrad tier. Must be one of: 985, 211, DOUBLE_NON, OTHER");
            }
        }
    }

}
