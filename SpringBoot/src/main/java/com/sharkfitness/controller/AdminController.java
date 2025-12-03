package com.sharkfitness.controller;

import com.sharkfitness.dto.UserProfileUpdateRequest;
import com.sharkfitness.entity.Dynamic;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.UnauthorizedException;
import com.sharkfitness.repository.DynamicRepository;
import com.sharkfitness.service.AdminService;
import com.sharkfitness.service.ModerationService;
import com.sharkfitness.vo.AdminUserVO;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.DynamicVO;
import com.sharkfitness.vo.UserStatisticsVO;
import com.sharkfitness.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for administrator operations
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    private final ModerationService moderationService;

    @Autowired
    private DynamicRepository dynamicRepository;
    
    /**
     * List all users with optional filters
     * GET /api/admin/users?role=coach&username=john
     */
    @GetMapping("/users")
    public ApiResponse<List<AdminUserVO>> listAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String username,
            HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        List<AdminUserVO> users = adminService.listAllUsers(role, username);
        return ApiResponse.success(users);
    }
    
    /**
     * Get specific user details
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id, HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        UserVO user = adminService.getUserById(id);
        return ApiResponse.success(user);
    }
    
    /**
     * Update user information
     * PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    public ApiResponse<UserVO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserProfileUpdateRequest updateRequest,
            HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        UserVO updatedUser = adminService.updateUser(id, updateRequest);
        return ApiResponse.success(updatedUser);
    }
    
    /**
     * Delete user account
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        adminService.deleteUser(id);
        return ApiResponse.success(null);
    }
    
    /**
     * Get system statistics
     * GET /api/admin/statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<UserStatisticsVO> getStatistics(HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        UserStatisticsVO statistics = adminService.getUserStatistics();
        return ApiResponse.success(statistics);
    }
    
    /**
     * Get moderation queue
     * GET /api/admin/moderation?page=0&size=20
     */
    @GetMapping("/moderation")
    public ApiResponse<Page<DynamicVO>> getModerationQueue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            // 2. 将 @PathVariable 改为 @RequestParam
            // 这样它就能读取 URL 中的 ?status=pending
            @RequestParam String status,

            HttpServletRequest request) {
        
        // Check admin authorization
        //checkAdminRole(request);
        
        Page<DynamicVO> moderationQueue = moderationService.getModerationQueue(status,page, size);
        return ApiResponse.success(moderationQueue);
    }
    
    /**
     * Approve content
     * POST /api/admin/moderation/{id}/approve
     */
    @PostMapping("/moderation/{id}/approve")
    public ApiResponse<DynamicVO> approveContent(@PathVariable Long id, HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        DynamicVO approvedDynamic = moderationService.approveContent(id);
        return ApiResponse.success(approvedDynamic);
    }
    
    /**
     * Reject content
     * POST /api/admin/moderation/{id}/reject
     */
    @PostMapping("/moderation/{id}/reject")
    public ApiResponse<DynamicVO> rejectContent(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        String reason = requestBody.get("reason");
        DynamicVO rejectedDynamic = moderationService.rejectContent(id, reason);
        return ApiResponse.success(rejectedDynamic);
    }
    
    /**
     * Get moderation history
     * GET /api/admin/moderation/history?page=0&size=20
     */
    @GetMapping("/moderation/history")
    public ApiResponse<Page<DynamicVO>> getModerationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        // Check admin authorization
        checkAdminRole(request);
        
        Page<DynamicVO> history = moderationService.getModerationHistory(page, size);
        return ApiResponse.success(history);
    }
    
    /**
     * Helper method to check if current user is admin
     */
    private void checkAdminRole(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            throw new UnauthorizedException("Admin access required");
        }
    }

    // GET
    //	http://localhost:8080/api/admin/moderation?status=pending&page=0&size=20
    // 增加状态查询接口
/*    @GetMapping("/moderation/filter")
    public ApiResponse<Page<DynamicVO>> getModerationByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        // Check admin authorization
        // checkAdminRole(request);

        Pageable pageable = PageRequest.of(page, size);

        Page<Dynamic> dynamics = dynamicRepository.findByStatusOrderByCreatedAtDesc(status, pageable);

        return dynamics.map(this::convertToVO);
    }*/

}
