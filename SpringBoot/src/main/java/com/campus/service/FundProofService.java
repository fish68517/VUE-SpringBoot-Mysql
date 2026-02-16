package com.campus.service;

import com.campus.dto.FundProofDTO;
import com.campus.entity.Activity;
import com.campus.entity.FundProof;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.FundProofRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Fund Proof Service
 * Handles fund proof submission and retrieval operations
 */
@Slf4j
@Service
@Transactional
public class FundProofService {

    @Autowired
    private FundProofRepository fundProofRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String UPLOAD_DIR = "uploads/fund-proofs/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Submit fund proof for an activity
     * Validates activity exists and user is the organizer
     * 
     * @param activityId Activity ID
     * @param file Proof file
     * @param description Usage description
     * @param username Username of the organizer
     * @return Created FundProofDTO
     * @throws BusinessException if validation fails
     */
    public FundProofDTO submitFundProof(Long activityId, MultipartFile file, String description, String username) {
        log.info("Submitting fund proof for activity {} by user {}", activityId, username);

        // Validate file
        if (file == null || file.isEmpty()) {
            log.warn("File is empty");
            throw new BusinessException(400, "File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("File size exceeds limit: {}", file.getSize());
            throw new BusinessException(400, "File size exceeds 10MB limit");
        }

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            throw new BusinessException(404, "User not found");
        }

        User user = userOptional.get();

        // Find activity
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            throw new BusinessException(404, "Activity not found");
        }

        Activity activity = activityOptional.get();

        // Verify user is the organizer
        if (!activity.getOrganizerId().equals(user.getId())) {
            log.warn("User {} is not the organizer of activity {}", username, activityId);
            throw new BusinessException(403, "Only the organizer can submit fund proof");
        }

        // Save file
        String fileUrl = saveFile(file);

        // Create fund proof
        FundProof fundProof = FundProof.builder()
                .activityId(activityId)
                .organizerId(user.getId())
                .fileUrl(fileUrl)
                .description(description)
                .status(FundProof.ProofStatus.PENDING_AUDIT)
                .build();

        FundProof savedProof = fundProofRepository.save(fundProof);
        log.info("Fund proof submitted successfully - ID: {}", savedProof.getId());

        return FundProofDTO.fromEntity(savedProof);
    }

    /**
     * Get fund proofs for an activity
     * 
     * @param activityId Activity ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated fund proofs
     */
    public Page<FundProofDTO> getActivityFundProofs(Long activityId, int page, int size) {
        log.info("Getting fund proofs for activity {} - page: {}, size: {}", activityId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<FundProof> proofs = fundProofRepository.findByActivityId(activityId, pageable);

        return proofs.map(FundProofDTO::fromEntity);
    }

    /**
     * Get fund proofs submitted by an organizer
     * 
     * @param organizerId Organizer ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated fund proofs
     */
    public Page<FundProofDTO> getOrganizerFundProofs(Long organizerId, int page, int size) {
        log.info("Getting fund proofs for organizer {} - page: {}, size: {}", organizerId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<FundProof> proofs = fundProofRepository.findByOrganizerId(organizerId, pageable);

        return proofs.map(FundProofDTO::fromEntity);
    }

    /**
     * Get fund proofs by status (for admin audit)
     * 
     * @param status Proof status
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated fund proofs
     */
    public Page<FundProofDTO> getFundProofsByStatus(FundProof.ProofStatus status, int page, int size) {
        log.info("Getting fund proofs by status {} - page: {}, size: {}", status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<FundProof> proofs = fundProofRepository.findByStatus(status, pageable);

        return proofs.map(FundProofDTO::fromEntity);
    }

    /**
     * Get fund proof by ID
     * 
     * @param id Fund proof ID
     * @return FundProofDTO
     * @throws BusinessException if not found
     */
    public FundProofDTO getFundProofById(Long id) {
        log.info("Getting fund proof by ID: {}", id);

        Optional<FundProof> proofOptional = fundProofRepository.findById(id);
        if (proofOptional.isEmpty()) {
            log.warn("Fund proof not found: {}", id);
            throw new BusinessException(404, "Fund proof not found");
        }

        return FundProofDTO.fromEntity(proofOptional.get());
    }

    /**
     * Save uploaded file to disk
     * 
     * @param file Uploaded file
     * @return File URL path
     * @throws BusinessException if file save fails
     */
    private String saveFile(MultipartFile file) {
        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String uniqueFilename = UUID.randomUUID() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.write(filePath, file.getBytes());

            log.info("File saved successfully: {}", filePath);
            return UPLOAD_DIR + uniqueFilename;
        } catch (IOException e) {
            log.error("Failed to save file", e);
            throw new BusinessException(500, "Failed to save file: " + e.getMessage());
        }
    }

}
