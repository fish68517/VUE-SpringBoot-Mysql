package com.sharkfitness.service;

import com.sharkfitness.dto.UserProfileUpdateRequest;
import com.sharkfitness.vo.AdminUserVO;
import com.sharkfitness.vo.UserStatisticsVO;
import com.sharkfitness.vo.UserVO;

import java.util.List;

/**
 * Service interface for administrator operations
 */
public interface AdminService {
    
    /**
     * List all users with optional filters
     * 
     * @param role Optional role filter (user, coach, admin)
     * @param username Optional username search filter
     * @return List of users with statistics
     */
    List<AdminUserVO> listAllUsers(String role, String username);
    
    /**
     * Get user details by ID
     * 
     * @param id User ID
     * @return User details
     */
    UserVO getUserById(Long id);
    
    /**
     * Update any user's profile (admin privilege)
     * 
     * @param id User ID
     * @param request Update request
     * @return Updated user details
     */
    UserVO updateUser(Long id, UserProfileUpdateRequest request);
    
    /**
     * Delete user and associated data
     * 
     * @param id User ID
     */
    void deleteUser(Long id);
    
    /**
     * Get system-wide user statistics
     * 
     * @return User statistics
     */
    UserStatisticsVO getUserStatistics();
}
