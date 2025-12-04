package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.UserDTO;
import com.shenyang.musicfestival.entity.User;

import java.util.Optional;

/**
 * Service interface for User operations
 */
public interface UserService {

    /**
     * Register a new user
     */
    User register(String phone, String password);

    /**
     * Find user by phone
     */
    Optional<User> findByPhone(String phone);

    /**
     * Find user by ID number
     */
    Optional<User> findByIdNumber(String idNumber);

    /**
     * Check if phone already exists
     */
    boolean phoneExists(String phone);

    /**
     * Check if ID number already exists
     */
    boolean idNumberExists(String idNumber);

    /**
     * Update user profile
     */
    User updateProfile(Long userId, UserDTO userDTO);

    /**
     * Verify user real name
     */
    User verifyRealName(Long userId, String realName, String idNumber);

    /**
     * Get user by ID
     */
    Optional<User> getUserById(Long userId);

    /**
     * Login user with phone and password
     */
    Optional<User> login(String phone, String password);

    /**
     * Upload user avatar
     */
    User uploadAvatar(Long userId, String avatarUrl);

}
