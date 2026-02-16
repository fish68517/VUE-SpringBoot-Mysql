package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.CrowdfundingTierDTO;
import com.campus.service.CrowdfundingTierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Crowdfunding Tier Controller
 * Handles crowdfunding tier management endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/crowdfunding/tiers")
@Tag(name = "Crowdfunding Tier Management", description = "Crowdfunding tier management endpoints")
public class CrowdfundingTierController {

    @Autowired
    private CrowdfundingTierService crowdfundingTierService;

    /**
     * Get crowdfunding tiers by activity ID
     * 
     * @param activityId Activity ID
     * @return ApiResponse with list of crowdfunding tiers
     */
    @GetMapping("/{activityId}")
    @Operation(summary = "Get crowdfunding tiers", description = "Retrieve all crowdfunding tiers for a specific activity")
    public ResponseEntity<ApiResponse<List<CrowdfundingTierDTO>>> getTiersByActivityId(
            @PathVariable("activityId") Long activityId) {
        log.info("Get crowdfunding tiers endpoint called - activity ID: {}", activityId);

        List<CrowdfundingTierDTO> tiers = crowdfundingTierService.getTiersByActivityId(activityId);

        return ResponseEntity.ok(ApiResponse.success("Crowdfunding tiers retrieved successfully", tiers));
    }

    /**
     * Get preset tiers by activity ID
     * 
     * @param activityId Activity ID
     * @return ApiResponse with list of preset crowdfunding tiers
     */
    @GetMapping("/{activityId}/preset")
    @Operation(summary = "Get preset tiers", description = "Retrieve preset crowdfunding tiers for a specific activity")
    public ResponseEntity<ApiResponse<List<CrowdfundingTierDTO>>> getPresetTiersByActivityId(
            @PathVariable("activityId") Long activityId) {
        log.info("Get preset crowdfunding tiers endpoint called - activity ID: {}", activityId);

        List<CrowdfundingTierDTO> tiers = crowdfundingTierService.getPresetTiersByActivityId(activityId);

        return ResponseEntity.ok(ApiResponse.success("Preset crowdfunding tiers retrieved successfully", tiers));
    }

    /**
     * Get custom tiers by activity ID
     * 
     * @param activityId Activity ID
     * @return ApiResponse with list of custom crowdfunding tiers
     */
    @GetMapping("/{activityId}/custom")
    @Operation(summary = "Get custom tiers", description = "Retrieve custom crowdfunding tiers for a specific activity")
    public ResponseEntity<ApiResponse<List<CrowdfundingTierDTO>>> getCustomTiersByActivityId(
            @PathVariable("activityId") Long activityId) {
        log.info("Get custom crowdfunding tiers endpoint called - activity ID: {}", activityId);

        List<CrowdfundingTierDTO> tiers = crowdfundingTierService.getCustomTiersByActivityId(activityId);

        return ResponseEntity.ok(ApiResponse.success("Custom crowdfunding tiers retrieved successfully", tiers));
    }

}
