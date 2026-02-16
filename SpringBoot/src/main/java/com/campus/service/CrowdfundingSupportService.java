package com.campus.service;

import com.campus.dto.ActivityDTO;
import com.campus.dto.CrowdfundingSupportDTO;
import com.campus.entity.Activity;
import com.campus.entity.CrowdfundingSupport;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.CrowdfundingSupportRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Crowdfunding Support Service
 * Handles business logic for crowdfunding support operations
 */
@Slf4j
@Service
@Transactional
public class CrowdfundingSupportService {

    @Autowired
    private CrowdfundingSupportRepository crowdfundingSupportRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new crowdfunding support record
     * 
     * @param supportDTO Crowdfunding support DTO
     * @param username Username of the supporter
     * @return Created crowdfunding support DTO
     * @throws BusinessException if validation fails
     */
    public CrowdfundingSupportDTO createSupport(CrowdfundingSupportDTO supportDTO, String username) {
        log.info("Creating crowdfunding support for activity ID: {} by user: {}", supportDTO.getActivityId(), username);

        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "User not found"));

        // Find activity
        Activity activity = activityRepository.findById(supportDTO.getActivityId())
                .orElseThrow(() -> new BusinessException(404, "Activity not found"));

        // Validate activity has crowdfunding enabled
        if (!activity.getEnableCrowdfunding()) {
            log.warn("Activity {} does not have crowdfunding enabled", supportDTO.getActivityId());
            throw new BusinessException(400, "This activity does not have crowdfunding enabled");
        }

        // Validate support amount
        if (supportDTO.getAmount() == null || supportDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "Support amount must be greater than 0");
        }

        // Create support record
        CrowdfundingSupport support = CrowdfundingSupport.builder()
                .activityId(supportDTO.getActivityId())
                .userId(user.getId())
                .amount(supportDTO.getAmount())
                .tierId(supportDTO.getTierId())
                .paymentStatus(CrowdfundingSupport.PaymentStatus.COMPLETED)
                .build();

        CrowdfundingSupport savedSupport = crowdfundingSupportRepository.save(support);
        log.info("Crowdfunding support created successfully - ID: {}", savedSupport.getId());

        return CrowdfundingSupportDTO.fromEntity(savedSupport);
    }

    /**
     * Get crowdfunding details for an activity
     * Includes total amount raised and completion percentage
     * 
     * @param activityId Activity ID
     * @return Crowdfunding details DTO
     * @throws BusinessException if activity not found
     */
    public CrowdfundingDetailsDTO getCrowdfundingDetails(Long activityId) {
        log.info("Getting crowdfunding details for activity ID: {}", activityId);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(404, "Activity not found"));

        if (!activity.getEnableCrowdfunding()) {
            log.warn("Activity {} does not have crowdfunding enabled", activityId);
            throw new BusinessException(400, "This activity does not have crowdfunding enabled");
        }

        BigDecimal totalAmount = crowdfundingSupportRepository.getTotalAmountByActivityId(activityId);
        long supporterCount = crowdfundingSupportRepository.countByActivityIdAndPaymentStatus(
                activityId, CrowdfundingSupport.PaymentStatus.COMPLETED);

        BigDecimal target = activity.getCrowdfundingTarget() != null ? activity.getCrowdfundingTarget() : BigDecimal.ZERO;
        BigDecimal completionPercentage = target.compareTo(BigDecimal.ZERO) > 0
                ? totalAmount.divide(target, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(100))
                : BigDecimal.ZERO;

        return CrowdfundingDetailsDTO.builder()
                .activityId(activityId)
                .targetAmount(target)
                .raisedAmount(totalAmount)
                .completionPercentage(completionPercentage)
                .supporterCount(supporterCount)
                .build();
    }

    /**
     * Get crowdfunding supports by activity ID
     * 
     * @param activityId Activity ID
     * @param pageable Pagination information
     * @return Page of crowdfunding support DTOs
     */
    public Page<CrowdfundingSupportDTO> getSupportsByActivityId(Long activityId, Pageable pageable) {
        log.info("Getting crowdfunding supports for activity ID: {}", activityId);

        return crowdfundingSupportRepository.findByActivityId(activityId, pageable)
                .map(support -> {
                    CrowdfundingSupportDTO dto = CrowdfundingSupportDTO.fromEntity(support);
                    try {
                        if (support.getActivityId() != null) {
                            Activity activity = activityRepository.findById(support.getActivityId()).orElse(null);
                            if (activity != null) {
                                ActivityDTO activityDTO = ActivityDTO.fromEntity(activity);
                                // Add crowdfunding details
                                BigDecimal totalAmount = crowdfundingSupportRepository.getTotalAmountByActivityId(activity.getId());
                                activityDTO.setCrowdfundingAmount(totalAmount);
                                dto.setActivity(activityDTO);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Failed to fetch activity details for support ID: {}", support.getId(), e);
                    }
                    return dto;
                });
    }

    /**
     * Get crowdfunding supports by user ID
     * 
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of crowdfunding support DTOs
     */
    public Page<CrowdfundingSupportDTO> getSupportsByUserId(Long userId, Pageable pageable) {
        log.info("Getting crowdfunding supports for user ID: {}", userId);

        return crowdfundingSupportRepository.findByUserId(userId, pageable)
                .map(support -> {
                    CrowdfundingSupportDTO dto = CrowdfundingSupportDTO.fromEntity(support);
                    try {
                        if (support.getActivityId() != null) {
                            Activity activity = activityRepository.findById(support.getActivityId()).orElse(null);
                            if (activity != null) {
                                ActivityDTO activityDTO = ActivityDTO.fromEntity(activity);
                                // Add crowdfunding details
                                BigDecimal totalAmount = crowdfundingSupportRepository.getTotalAmountByActivityId(activity.getId());
                                activityDTO.setCrowdfundingAmount(totalAmount);
                                dto.setActivity(activityDTO);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Failed to fetch activity details for support ID: {}", support.getId(), e);
                    }
                    return dto;
                });
    }

    /**
     * Get crowdfunding supports by current user
     * 
     * @param username Username
     * @param pageable Pagination information
     * @return Page of crowdfunding support DTOs
     */
    public Page<CrowdfundingSupportDTO> getMySupports(String username, Pageable pageable) {
        log.info("Getting crowdfunding supports for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "User not found"));

        return getSupportsByUserId(user.getId(), pageable);
    }

    /**
     * Crowdfunding Details DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CrowdfundingDetailsDTO {
        private Long activityId;
        private BigDecimal targetAmount;
        private BigDecimal raisedAmount;
        private BigDecimal completionPercentage;
        private Long supporterCount;
    }

}
