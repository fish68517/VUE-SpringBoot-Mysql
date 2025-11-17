package com.sharkfitness.service.impl;

import com.sharkfitness.dto.UserProfileUpdateRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.repository.*;
import com.sharkfitness.service.AdminService;
import com.sharkfitness.vo.AdminUserVO;
import com.sharkfitness.vo.UserStatisticsVO;
import com.sharkfitness.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of admin service
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final UserRepository userRepository;
    private final CoachStudentRepository coachStudentRepository;
    private final ResourceRepository resourceRepository;
    private final DynamicRepository dynamicRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final CheckInRepository checkInRepository;
    private final DietRecordRepository dietRecordRepository;
    private final CollectionRepository collectionRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    
    @Override
    public List<AdminUserVO> listAllUsers(String role, String username) {
        List<User> users;
        
        // Apply filters
        if (role != null && !role.isEmpty() && username != null && !username.isEmpty()) {
            users = userRepository.findByRoleAndUsernameContainingIgnoreCase(role, username);
        } else if (role != null && !role.isEmpty()) {
            users = userRepository.findByRole(role);
        } else if (username != null && !username.isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCase(username);
        } else {
            users = userRepository.findAll();
        }
        
        // Convert to AdminUserVO with statistics
        return users.stream()
            .map(this::convertToAdminUserVO)
            .collect(Collectors.toList());
    }
    
    @Override
    public UserVO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return convertToUserVO(user);
    }
    
    @Override
    @Transactional
    public UserVO updateUser(Long id, UserProfileUpdateRequest request) {
        User user = userRepository.findById(id)
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
        
        return convertToUserVO(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Delete associated data
        // Delete check-ins
        checkInRepository.deleteByUserId(id);
        
        // Delete diet records
        dietRecordRepository.deleteByUserId(id);
        
        // Delete collections
        collectionRepository.deleteByUserId(id);
        
        // Delete comments
        commentRepository.deleteByUserId(id);
        
        // Delete likes
        likeRepository.deleteByUserId(id);
        
        // Delete coach-student relationships (both as coach and student)
        coachStudentRepository.deleteByCoachId(id);
        coachStudentRepository.deleteByStudentId(id);
        
        // Delete training plans (both created and assigned)
        trainingPlanRepository.deleteByCoachId(id);
        trainingPlanRepository.deleteByStudentId(id);
        
        // Delete dynamic posts
        dynamicRepository.deleteByUserId(id);
        
        // Delete resources created by user
        resourceRepository.deleteByCreatorId(id);
        
        // Finally, delete the user
        userRepository.delete(user);
    }
    
    @Override
    public UserStatisticsVO getUserStatistics() {
        Long totalUsers = userRepository.count();
        Long totalRegularUsers = userRepository.countByRole("user");
        Long totalCoaches = userRepository.countByRole("coach");
        Long totalAdmins = userRepository.countByRole("admin");
        Long totalResources = resourceRepository.count();
        Long totalDynamicPosts = dynamicRepository.count();
        Long totalTrainingPlans = trainingPlanRepository.count();
        
        return new UserStatisticsVO(
            totalUsers,
            totalRegularUsers,
            totalCoaches,
            totalAdmins,
            totalResources,
            totalDynamicPosts,
            totalTrainingPlans
        );
    }
    
    /**
     * Convert User entity to AdminUserVO with statistics
     */
    private AdminUserVO convertToAdminUserVO(User user) {
        Long studentCount = 0L;
        Long contentCount = 0L;
        
        // Calculate statistics for coaches
        if ("coach".equals(user.getRole())) {
            studentCount = (long) coachStudentRepository.findByCoachId(user.getId()).size();
            contentCount = resourceRepository.countByCreator(user);
        }
        
        return new AdminUserVO(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getAvatar(),
            user.getGender(),
            user.getIntro(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            studentCount,
            contentCount
        );
    }
    
    /**
     * Convert User entity to UserVO
     */
    private UserVO convertToUserVO(User user) {
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
