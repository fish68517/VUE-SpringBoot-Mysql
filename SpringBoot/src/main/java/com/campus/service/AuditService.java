package com.campus.service;

import com.campus.dto.ActivityDTO;
import com.campus.dto.AuditLogDTO;
import com.campus.dto.FundProofDTO;
import com.campus.entity.Activity;
import com.campus.entity.AuditLog;
import com.campus.entity.FundProof;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.AuditLogRepository;
import com.campus.repository.FundProofRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Audit Service
 * Handles audit operations for activities and fund proofs
 */
@Slf4j
@Service
@Transactional
public class AuditService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FundProofRepository fundProofRepository;

    /**
     * Get pending audit activities list
     * Only admins can access this
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of ActivityDTO with PENDING_AUDIT status
     */
    public Page<ActivityDTO> getPendingAuditActivities(int page, int size) {
        log.info("Fetching pending audit activities - page: {}, size: {}", page, size);

        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        // Create pageable with sorting by creation time (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Query activities with PENDING_AUDIT status
        Page<Activity> activityPage = activityRepository.findByStatus(Activity.ActivityStatus.PENDING_AUDIT, pageable);

        // Convert to DTO
        return activityPage.map(ActivityDTO::fromEntity);
    }

    /**
     * Audit an activity (approve or reject)
     * Only admins can perform this operation
     * 
     * @param activityId Activity ID
     * @param approved Whether to approve (true) or reject (false)
     * @param reason Reason for rejection (required if rejected)
     * @param auditorUsername Username of the admin performing the audit
     * @return Updated ActivityDTO
     * @throws BusinessException if activity not found, unauthorized, or validation fails
     */
    public ActivityDTO auditActivity(Long activityId, boolean approved, String reason, String auditorUsername) {
        log.info("Auditing activity: {} - approved: {}, auditor: {}", activityId, approved, auditorUsername);

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Check if activity is in PENDING_AUDIT status
        if (activity.getStatus() != Activity.ActivityStatus.PENDING_AUDIT) {
            log.warn("Activity {} is not in PENDING_AUDIT status", activityId);
            throw new BusinessException(400, "Activity is not pending audit");
        }

        // Find auditor
        Optional<User> auditorOptional = userRepository.findByUsername(auditorUsername);
        if (auditorOptional.isEmpty()) {
            log.warn("Auditor not found: {}", auditorUsername);
            throw new BusinessException(404, "Auditor not found");
        }

        User auditor = auditorOptional.get();

        // Validate auditor role
        if (auditor.getRole() != User.UserRole.ADMIN) {
            log.warn("User {} is not an admin", auditorUsername);
            throw new BusinessException(403, "Only admins can audit activities");
        }

        // Validate rejection reason
        if (!approved && (reason == null || reason.trim().isEmpty())) {
            throw new BusinessException(400, "Rejection reason is required");
        }

        // Update activity status
        if (approved) {
            activity.setStatus(Activity.ActivityStatus.APPROVED);
            log.info("Activity {} approved", activityId);
        } else {
            activity.setStatus(Activity.ActivityStatus.REJECTED);
            log.info("Activity {} rejected with reason: {}", activityId, reason);
        }

        // Save updated activity
        Activity updatedActivity = activityRepository.save(activity);

        // Create audit log
        AuditLog auditLog = AuditLog.builder()
                .auditorId(auditor.getId())
                .targetId(activityId)
                .auditType(AuditLog.AuditType.ACTIVITY)
                .auditStatus(approved ? AuditLog.AuditStatus.APPROVED : AuditLog.AuditStatus.REJECTED)
                .reason(reason)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log created for activity {}", activityId);

        return ActivityDTO.fromEntity(updatedActivity);
    }

    /**
     * Get audit logs for an activity
     * 
     * @param activityId Activity ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of AuditLogDTO
     */
    public Page<AuditLogDTO> getActivityAuditLogs(Long activityId, int page, int size) {
        log.info("Fetching audit logs for activity: {} - page: {}, size: {}", activityId, page, size);

        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        // Create pageable with sorting by creation time (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Query audit logs for the activity
        Page<AuditLog> auditLogPage = auditLogRepository.findByTargetId(activityId, pageable);

        // Convert to DTO
        return auditLogPage.map(AuditLogDTO::fromEntity);
    }

    /**
     * Get pending audit fund proofs list
     * Only admins can access this
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of FundProofDTO with PENDING_AUDIT status
     */
    public Page<FundProofDTO> getPendingAuditFundProofs(int page, int size) {
        log.info("Fetching pending audit fund proofs - page: {}, size: {}", page, size);

        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        // Create pageable with sorting by creation time (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Query fund proofs with PENDING_AUDIT status
        Page<FundProof> fundProofPage = fundProofRepository.findByStatus(FundProof.ProofStatus.PENDING_AUDIT, pageable);

        // Convert to DTO
        return fundProofPage.map(FundProofDTO::fromEntity);
    }

    /**
     * Audit a fund proof (approve or reject)
     * Only admins can perform this operation
     * 
     * @param fundProofId Fund proof ID
     * @param approved Whether to approve (true) or reject (false)
     * @param reason Reason for rejection (required if rejected)
     * @param auditorUsername Username of the admin performing the audit
     * @return Updated FundProofDTO
     * @throws BusinessException if fund proof not found, unauthorized, or validation fails
     */
    public FundProofDTO auditFundProof(Long fundProofId, boolean approved, String reason, String auditorUsername) {
        log.info("Auditing fund proof: {} - approved: {}, auditor: {}", fundProofId, approved, auditorUsername);

        // Find fund proof
        Optional<FundProof> fundProofOptional = fundProofRepository.findById(fundProofId);
        if (fundProofOptional.isEmpty()) {
            log.warn("Fund proof not found: {}", fundProofId);
            throw new BusinessException(404, "Fund proof not found");
        }

        FundProof fundProof = fundProofOptional.get();

        // Check if fund proof is in PENDING_AUDIT status
        if (fundProof.getStatus() != FundProof.ProofStatus.PENDING_AUDIT) {
            log.warn("Fund proof {} is not in PENDING_AUDIT status", fundProofId);
            throw new BusinessException(400, "Fund proof is not pending audit");
        }

        // Find auditor
        Optional<User> auditorOptional = userRepository.findByUsername(auditorUsername);
        if (auditorOptional.isEmpty()) {
            log.warn("Auditor not found: {}", auditorUsername);
            throw new BusinessException(404, "Auditor not found");
        }

        User auditor = auditorOptional.get();

        // Validate auditor role
        if (auditor.getRole() != User.UserRole.ADMIN) {
            log.warn("User {} is not an admin", auditorUsername);
            throw new BusinessException(403, "Only admins can audit fund proofs");
        }

        // Validate rejection reason
        if (!approved && (reason == null || reason.trim().isEmpty())) {
            throw new BusinessException(400, "Rejection reason is required");
        }

        // Update fund proof status
        if (approved) {
            fundProof.setStatus(FundProof.ProofStatus.APPROVED);
            log.info("Fund proof {} approved", fundProofId);
        } else {
            fundProof.setStatus(FundProof.ProofStatus.REJECTED);
            fundProof.setRejectionReason(reason);
            log.info("Fund proof {} rejected with reason: {}", fundProofId, reason);
        }

        // Save updated fund proof
        FundProof updatedFundProof = fundProofRepository.save(fundProof);

        // Create audit log
        AuditLog auditLog = AuditLog.builder()
                .auditorId(auditor.getId())
                .targetId(fundProofId)
                .auditType(AuditLog.AuditType.FUND_PROOF)
                .auditStatus(approved ? AuditLog.AuditStatus.APPROVED : AuditLog.AuditStatus.REJECTED)
                .reason(reason)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log created for fund proof {}", fundProofId);

        return FundProofDTO.fromEntity(updatedFundProof);
    }

}
