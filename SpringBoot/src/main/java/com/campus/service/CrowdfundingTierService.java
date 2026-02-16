package com.campus.service;

import com.campus.dto.CrowdfundingTierDTO;
import com.campus.entity.CrowdfundingTier;
import com.campus.repository.CrowdfundingTierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Crowdfunding Tier Service
 * Handles business logic for crowdfunding tiers
 */
@Slf4j
@Service
@Transactional
public class CrowdfundingTierService {

    @Autowired
    private CrowdfundingTierRepository crowdfundingTierRepository;

    /**
     * Get crowdfunding tiers by activity ID
     * 
     * @param activityId Activity ID
     * @return List of crowdfunding tier DTOs
     */
    public List<CrowdfundingTierDTO> getTiersByActivityId(Long activityId) {
        log.info("Getting crowdfunding tiers for activity ID: {}", activityId);
        
        List<CrowdfundingTier> tiers = crowdfundingTierRepository.findByActivityIdOrderByDisplayOrder(activityId);
        
        return tiers.stream()
                .map(CrowdfundingTierDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get preset tiers by activity ID
     * 
     * @param activityId Activity ID
     * @return List of preset crowdfunding tier DTOs
     */
    public List<CrowdfundingTierDTO> getPresetTiersByActivityId(Long activityId) {
        log.info("Getting preset crowdfunding tiers for activity ID: {}", activityId);
        
        List<CrowdfundingTier> tiers = crowdfundingTierRepository.findPresetTiersByActivityId(activityId);
        
        return tiers.stream()
                .map(CrowdfundingTierDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get custom tiers by activity ID
     * 
     * @param activityId Activity ID
     * @return List of custom crowdfunding tier DTOs
     */
    public List<CrowdfundingTierDTO> getCustomTiersByActivityId(Long activityId) {
        log.info("Getting custom crowdfunding tiers for activity ID: {}", activityId);
        
        List<CrowdfundingTier> tiers = crowdfundingTierRepository.findCustomTiersByActivityId(activityId);
        
        return tiers.stream()
                .map(CrowdfundingTierDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Create a new crowdfunding tier
     * 
     * @param tierDTO Crowdfunding tier DTO
     * @return Created crowdfunding tier DTO
     */
    public CrowdfundingTierDTO createTier(CrowdfundingTierDTO tierDTO) {
        log.info("Creating crowdfunding tier for activity ID: {}", tierDTO.getActivityId());
        
        CrowdfundingTier tier = tierDTO.toEntity();
        CrowdfundingTier savedTier = crowdfundingTierRepository.save(tier);
        
        return CrowdfundingTierDTO.fromEntity(savedTier);
    }

    /**
     * Update an existing crowdfunding tier
     * 
     * @param tierId Tier ID
     * @param tierDTO Updated tier DTO
     * @return Updated crowdfunding tier DTO
     */
    public CrowdfundingTierDTO updateTier(Long tierId, CrowdfundingTierDTO tierDTO) {
        log.info("Updating crowdfunding tier ID: {}", tierId);
        
        CrowdfundingTier tier = crowdfundingTierRepository.findById(tierId)
                .orElseThrow(() -> new RuntimeException("Crowdfunding tier not found"));
        
        if (tierDTO.getName() != null) {
            tier.setName(tierDTO.getName());
        }
        if (tierDTO.getAmount() != null) {
            tier.setAmount(tierDTO.getAmount());
        }
        if (tierDTO.getDescription() != null) {
            tier.setDescription(tierDTO.getDescription());
        }
        if (tierDTO.getDisplayOrder() != null) {
            tier.setDisplayOrder(tierDTO.getDisplayOrder());
        }
        
        CrowdfundingTier updatedTier = crowdfundingTierRepository.save(tier);
        
        return CrowdfundingTierDTO.fromEntity(updatedTier);
    }

    /**
     * Delete a crowdfunding tier
     * 
     * @param tierId Tier ID
     */
    public void deleteTier(Long tierId) {
        log.info("Deleting crowdfunding tier ID: {}", tierId);
        
        crowdfundingTierRepository.deleteById(tierId);
    }

    /**
     * Initialize default preset tiers for an activity
     * 
     * @param activityId Activity ID
     */
    public void initializeDefaultTiers(Long activityId) {
        log.info("Initializing default preset tiers for activity ID: {}", activityId);
        
        // Create default preset tiers: 10元, 50元, 100元
        CrowdfundingTier tier1 = CrowdfundingTier.builder()
                .activityId(activityId)
                .name("10元档")
                .amount(java.math.BigDecimal.valueOf(10))
                .description("支持10元")
                .isPreset(true)
                .displayOrder(1)
                .build();
        
        CrowdfundingTier tier2 = CrowdfundingTier.builder()
                .activityId(activityId)
                .name("50元档")
                .amount(java.math.BigDecimal.valueOf(50))
                .description("支持50元")
                .isPreset(true)
                .displayOrder(2)
                .build();
        
        CrowdfundingTier tier3 = CrowdfundingTier.builder()
                .activityId(activityId)
                .name("100元档")
                .amount(java.math.BigDecimal.valueOf(100))
                .description("支持100元")
                .isPreset(true)
                .displayOrder(3)
                .build();
        
        crowdfundingTierRepository.save(tier1);
        crowdfundingTierRepository.save(tier2);
        crowdfundingTierRepository.save(tier3);
    }

}
