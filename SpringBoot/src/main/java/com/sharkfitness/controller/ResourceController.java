package com.sharkfitness.controller;

import com.sharkfitness.dto.ResourceCreateRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.service.ResourceService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.ResourceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controller for fitness resource management
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {
    
    private final ResourceService resourceService;
    
    /**
     * Get all resources with pagination
     */
    @GetMapping
    public ApiResponse<Page<ResourceVO>> getAllResources(
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<ResourceVO> resources;
        if (contentType != null && !contentType.isEmpty()) {
            resources = resourceService.findByContentType(contentType, pageable);
        } else {
            resources = resourceService.findAll(pageable);
        }
        
        return ApiResponse.success(resources);
    }
    
    /**
     * Get resource by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<ResourceVO> getResourceById(@PathVariable Long id) {
        // Increment view count when resource is viewed
        resourceService.incrementViewCount(id);
        ResourceVO resource = resourceService.findById(id);
        return ApiResponse.success(resource);
    }
    
    /**
     * Create a new resource (Admin/Coach only)
     */
    @PostMapping
    public ApiResponse<ResourceVO> createResource(
            @Valid @RequestBody ResourceCreateRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        ResourceVO resource = resourceService.create(request, currentUser.getId());
        return ApiResponse.success(resource);
    }
    
    /**
     * Update a resource (Admin or creator)
     */
    @PutMapping("/{id}")
    public ApiResponse<ResourceVO> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceCreateRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        ResourceVO resource = resourceService.update(id, request, currentUser.getId());
        return ApiResponse.success(resource);
    }
    
    /**
     * Delete a resource (Admin only)
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteResource(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        
        resourceService.delete(id, currentUser.getId());
        return ApiResponse.success(null);
    }
}
