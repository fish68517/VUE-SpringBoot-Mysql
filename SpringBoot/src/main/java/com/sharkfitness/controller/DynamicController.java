package com.sharkfitness.controller;

import com.sharkfitness.dto.DynamicCreateRequest;
import com.sharkfitness.service.DynamicService;
import com.sharkfitness.service.LikeService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.DynamicVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for dynamic post operations
 */
@RestController
@RequestMapping("/api/dynamics")
@RequiredArgsConstructor
public class DynamicController {
    
    private final DynamicService dynamicService;
    private final LikeService likeService;
    
    /**
     * Get all dynamic posts with pagination
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @return API response with page of dynamic posts
     */
    @GetMapping
    public ApiResponse<Page<DynamicVO>> getAllDynamics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DynamicVO> dynamics = dynamicService.findAll(pageable);
        return ApiResponse.success(dynamics);
    }
    
    /**
     * Get dynamic post by ID
     * @param id the dynamic post ID
     * @return API response with dynamic post
     */
    @GetMapping("/{id}")
    public ApiResponse<DynamicVO> getDynamicById(@PathVariable Long id) {
        DynamicVO dynamic = dynamicService.findById(id);
        return ApiResponse.success(dynamic);
    }
    
    /**
     * Create a new dynamic post
     * @param request HTTP request containing current user ID
     * @param createRequest dynamic creation request
     * @return API response with created dynamic post
     */
    @PostMapping
    public ApiResponse<DynamicVO> createDynamic(
            HttpServletRequest request,
            @Valid @RequestBody DynamicCreateRequest createRequest) {
        Long userId = (Long) request.getAttribute("currentUserId");
        DynamicVO dynamic = dynamicService.create(userId, createRequest);
        return ApiResponse.success(dynamic);
    }
    
    /**
     * Update a dynamic post
     * @param id the dynamic post ID
     * @param request HTTP request containing current user ID
     * @param updateRequest dynamic update request
     * @return API response with updated dynamic post
     */
    @PutMapping("/{id}")
    public ApiResponse<DynamicVO> updateDynamic(
            @PathVariable Long id,
            HttpServletRequest request,
            @Valid @RequestBody DynamicCreateRequest updateRequest) {
        Long userId = (Long) request.getAttribute("currentUserId");
        DynamicVO dynamic = dynamicService.update(id, userId, updateRequest);
        return ApiResponse.success(dynamic);
    }
    
    /**
     * Delete a dynamic post
     * @param id the dynamic post ID
     * @param request HTTP request containing current user ID
     * @return API response with success message
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDynamic(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        dynamicService.delete(id, userId);
        return ApiResponse.success(null);
    }
    
    /**
     * Like a dynamic post
     * @param id the dynamic post ID
     * @param request HTTP request containing current user ID
     * @return API response with success message
     */
    @PostMapping("/{id}/like")
    public ApiResponse<Void> likeDynamic(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        likeService.likePost(userId, id);
        return ApiResponse.success(null);
    }
    
    /**
     * Unlike a dynamic post
     * @param id the dynamic post ID
     * @param request HTTP request containing current user ID
     * @return API response with success message
     */
    @DeleteMapping("/{id}/like")
    public ApiResponse<Void> unlikeDynamic(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        likeService.unlikePost(userId, id);
        return ApiResponse.success(null);
    }
}
