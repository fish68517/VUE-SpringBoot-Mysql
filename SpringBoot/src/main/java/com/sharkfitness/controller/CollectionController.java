package com.sharkfitness.controller;

import com.sharkfitness.entity.User;
import com.sharkfitness.service.CollectionService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.ResourceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for collection management
 */
@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {
    
    private final CollectionService collectionService;
    
    /**
     * Get user's collections
     */
    @GetMapping
    public ApiResponse<List<ResourceVO>> getUserCollections(
            @RequestAttribute("currentUser") User currentUser) {
        
        List<ResourceVO> collections = collectionService.getUserCollections(currentUser.getId());
        return ApiResponse.success(collections);
    }
    
    /**
     * Add resource to collection
     */
    @PostMapping
    public ApiResponse<Void> addToCollection(
            @RequestParam Long resourceId,
            @RequestAttribute("currentUser") User currentUser) {
        
        collectionService.addToCollection(currentUser.getId(), resourceId);
        return ApiResponse.success(null);
    }
    
    /**
     * Remove resource from collection
     */
    @DeleteMapping("/{resourceId}")
    public ApiResponse<Void> removeFromCollection(
            @PathVariable Long resourceId,
            @RequestAttribute("currentUser") User currentUser) {
        
        collectionService.removeFromCollection(currentUser.getId(), resourceId);
        return ApiResponse.success(null);
    }
    
    /**
     * Check if resource is in collection
     */
    @GetMapping("/check/{resourceId}")
    public ApiResponse<Boolean> checkInCollection(
            @PathVariable Long resourceId,
            @RequestAttribute("currentUser") User currentUser) {
        
        boolean inCollection = collectionService.isInCollection(currentUser.getId(), resourceId);
        return ApiResponse.success(inCollection);
    }
}
