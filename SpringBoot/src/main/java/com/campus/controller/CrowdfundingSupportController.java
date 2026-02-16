package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.CrowdfundingSupportDTO;
import com.campus.service.CrowdfundingSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Crowdfunding Support Controller
 * Handles crowdfunding support endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/crowdfunding")
@Tag(name = "Crowdfunding Support Management", description = "Crowdfunding support management endpoints")
public class CrowdfundingSupportController {

    @Autowired
    private CrowdfundingSupportService crowdfundingSupportService;

    /**
     * Create a new crowdfunding support record
     * 
     * @param supportDTO Crowdfunding support DTO
     * @param authentication Authentication object
     * @return ApiResponse with created support DTO
     */
    @PostMapping("/support")
    @Operation(summary = "Create crowdfunding support", description = "Create a new crowdfunding support record")
    public ResponseEntity<ApiResponse<CrowdfundingSupportDTO>> createSupport(
            @RequestBody CrowdfundingSupportDTO supportDTO,
            Authentication authentication) {
        log.info("Create crowdfunding support endpoint called");

        String username = authentication.getName();
        CrowdfundingSupportDTO createdSupport = crowdfundingSupportService.createSupport(supportDTO, username);

        return ResponseEntity.ok(ApiResponse.success("Crowdfunding support created successfully", createdSupport));
    }

    /**
     * Get crowdfunding details for an activity
     * 
     * @param activityId Activity ID
     * @return ApiResponse with crowdfunding details
     */
    @GetMapping("/activity/{activityId}")
    @Operation(summary = "Get crowdfunding details", description = "Retrieve crowdfunding details for a specific activity")
    public ResponseEntity<ApiResponse<CrowdfundingSupportService.CrowdfundingDetailsDTO>> getCrowdfundingDetails(
            @PathVariable("activityId") Long activityId) {
        log.info("Get crowdfunding details endpoint called - activity ID: {}", activityId);

        CrowdfundingSupportService.CrowdfundingDetailsDTO details = crowdfundingSupportService.getCrowdfundingDetails(activityId);

        return ResponseEntity.ok(ApiResponse.success("Crowdfunding details retrieved successfully", details));
    }

    /**
     * Get crowdfunding supports by activity ID
     * 
     * @param activityId Activity ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with page of supports
     */
    @GetMapping("/activity/{activityId}/supports")
    @Operation(summary = "Get supports by activity", description = "Retrieve crowdfunding supports for a specific activity")
    public ResponseEntity<ApiResponse<Page<CrowdfundingSupportDTO>>> getSupportsByActivityId(
            @PathVariable("activityId") Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get supports by activity endpoint called - activity ID: {}", activityId);

        Pageable pageable = PageRequest.of(page, size);
        Page<CrowdfundingSupportDTO> supports = crowdfundingSupportService.getSupportsByActivityId(activityId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Crowdfunding supports retrieved successfully", supports));
    }

    /**
     * Get current user's crowdfunding supports
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param authentication Authentication object
     * @return ApiResponse with page of supports
     */
    @GetMapping("/my")
    @Operation(summary = "Get my crowdfunding supports", description = "Retrieve current user's crowdfunding supports")
    public ResponseEntity<ApiResponse<Page<CrowdfundingSupportDTO>>> getMySupports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        log.info("Get my crowdfunding supports endpoint called");

        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<CrowdfundingSupportDTO> supports = crowdfundingSupportService.getMySupports(username, pageable);

        return ResponseEntity.ok(ApiResponse.success("My crowdfunding supports retrieved successfully", supports));
    }

}
