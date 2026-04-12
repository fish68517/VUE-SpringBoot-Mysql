package com.travelMemory.service;

import com.travelMemory.dto.CreateTravelRecordRequest;
import com.travelMemory.dto.TravelRecordResponse;
import com.travelMemory.dto.UserInfo;
import com.travelMemory.entity.TravelRecord;
import com.travelMemory.entity.User;
import com.travelMemory.repository.CommentRepository;
import com.travelMemory.repository.LikeRepository;
import com.travelMemory.repository.TravelRecordRepository;
import com.travelMemory.repository.UserRepository;
import com.travelMemory.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelService {

    private final TravelRecordRepository travelRecordRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final FootprintService footprintService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    /**
     * Create a new travel record
     * @param userId the user ID
     * @param request the create travel record request
     * @return TravelRecordResponse containing the created record
     * @throws IllegalArgumentException if validation fails
     */
    public TravelRecordResponse createTravelRecord(Long userId, CreateTravelRecordRequest request) {
        // Validate user ID
        SecurityUtils.validateNumericId(userId);

        // Sanitize and validate input
        String title = SecurityUtils.sanitizeText(request.getTitle());
        String destination = SecurityUtils.sanitizeText(request.getDestination());
        String description = SecurityUtils.sanitizeText(request.getDescription());
        String diaryContent = SecurityUtils.sanitizeHtml(request.getDiaryContent());

        // Validate dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Create new travel record
        TravelRecord travelRecord = TravelRecord.builder()
                .userId(userId)
                .title(title)
                .destination(destination)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(description)
                .diaryContent(diaryContent)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .viewCount(0)
                .build();

        // Save to database (using parameterized queries via JPA)
        TravelRecord savedRecord = travelRecordRepository.save(travelRecord);

        // Convert to response DTO
        return TravelRecordResponse.from(savedRecord);
    }

    /**
     * Get travel records for a user with pagination
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of travel records
     */
    @Transactional(readOnly = true)
    public Page<TravelRecordResponse> getUserTravelRecords(Long userId, Pageable pageable) {
        Page<TravelRecord> records = travelRecordRepository.findByUserId(userId, pageable);
        return records.map(record -> {
            TravelRecordResponse response = TravelRecordResponse.from(record);
            response.setLikeCount((int) likeRepository.countByTravelRecordId(record.getId()));
            response.setCommentCount((int) commentRepository.countByTravelRecordId(record.getId()));
            return response;
        });
    }

    /**
     * Get a travel record by ID
     * @param recordId the record ID
     * @return TravelRecordResponse
     * @throws IllegalArgumentException if record not found or access denied
     */
    @Transactional(readOnly = true)
    public TravelRecordResponse getTravelRecordById(Long recordId) {
        TravelRecord record = travelRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found"));
        TravelRecordResponse response = TravelRecordResponse.from(record);
        response.setLikeCount((int) likeRepository.countByTravelRecordId(record.getId()));
        response.setCommentCount((int) commentRepository.countByTravelRecordId(record.getId()));
        return response;
    }

    /**
     * Get a travel record by ID with permission check
     * @param recordId the record ID
     * @param userId the user ID (for permission check)
     * @return TravelRecordResponse
     * @throws IllegalArgumentException if record not found or access denied
     */
    @Transactional(readOnly = true)
    public TravelRecordResponse getTravelRecordByIdWithPermissionCheck(Long recordId, Long userId) {
        TravelRecord record = travelRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found"));

        // Allow access if user is the owner or record is public
        if (!record.getIsPublic() && !record.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: This record is private");
        }

        TravelRecordResponse response = TravelRecordResponse.from(record);
        response.setLikeCount((int) likeRepository.countByTravelRecordId(record.getId()));
        response.setCommentCount((int) commentRepository.countByTravelRecordId(record.getId()));
        return response;
    }

    /**
     * Get a travel record by ID and verify ownership
     * @param recordId the record ID
     * @param userId the user ID
     * @return TravelRecordResponse
     * @throws IllegalArgumentException if record not found or user is not the owner
     */
    @Transactional(readOnly = true)
    public TravelRecordResponse getTravelRecordByIdAndUserId(Long recordId, Long userId) {
        TravelRecord record = travelRecordRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));
        TravelRecordResponse response = TravelRecordResponse.from(record);
        response.setLikeCount((int) likeRepository.countByTravelRecordId(record.getId()));
        response.setCommentCount((int) commentRepository.countByTravelRecordId(record.getId()));
        return response;
    }

    /**
     * Get public travel records with pagination
     * @param pageable pagination information
     * @return Page of public travel records
     */
    @Transactional(readOnly = true)
    public Page<TravelRecordResponse> getPublicTravelRecords(Pageable pageable) {
        Page<TravelRecord> records = travelRecordRepository.findByIsPublicTrue(pageable);
        
        // Convert to response DTOs with user information
        List<TravelRecordResponse> responses = records.getContent().stream()
                .map(record -> {
                    User user = userRepository.findById(record.getUserId()).orElse(null);
                    UserInfo userInfo = user != null ? UserInfo.from(user) : null;
                    TravelRecordResponse response = TravelRecordResponse.from(record, userInfo);
                    response.setLikeCount((int) likeRepository.countByTravelRecordId(record.getId()));
                    response.setCommentCount((int) commentRepository.countByTravelRecordId(record.getId()));
                    return response;
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, records.getTotalElements());
    }

    /**
     * Update a travel record
     * @param recordId the record ID
     * @param userId the user ID (for ownership verification)
     * @param request the update travel record request
     * @return TravelRecordResponse containing the updated record
     * @throws IllegalArgumentException if record not found, user is not the owner, or validation fails
     */
    public TravelRecordResponse updateTravelRecord(Long recordId, Long userId, CreateTravelRecordRequest request) {
        // Validate IDs
        SecurityUtils.validateNumericId(recordId);
        SecurityUtils.validateNumericId(userId);

        // Verify ownership (using parameterized query via JPA)
        TravelRecord record = travelRecordRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        // Sanitize and validate input
        String title = SecurityUtils.sanitizeText(request.getTitle());
        String destination = SecurityUtils.sanitizeText(request.getDestination());
        String description = SecurityUtils.sanitizeText(request.getDescription());
        String diaryContent = SecurityUtils.sanitizeHtml(request.getDiaryContent());

        // Validate dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Update fields
        record.setTitle(title);
        record.setDestination(destination);
        record.setStartDate(request.getStartDate());
        record.setEndDate(request.getEndDate());
        record.setDescription(description);
        record.setDiaryContent(diaryContent);
        if (request.getIsPublic() != null) {
            record.setIsPublic(request.getIsPublic());
        }

        // Save to database (using parameterized queries via JPA)
        TravelRecord updatedRecord = travelRecordRepository.save(record);

        // Convert to response DTO
        return TravelRecordResponse.from(updatedRecord);
    }

    /**
     * Delete a travel record
     * @param recordId the record ID
     * @param userId the user ID (for ownership verification)
     * @throws IllegalArgumentException if record not found or user is not the owner
     */
    public void deleteTravelRecord(Long recordId, Long userId) {
        // Verify ownership
        TravelRecord record = travelRecordRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        // Delete associated multimedia files
        try {
            fileService.deleteFilesByTravelRecord(recordId);
        } catch (IOException e) {
            // Log error but continue with record deletion
            System.err.println("Failed to delete multimedia files for record " + recordId + ": " + e.getMessage());
        }

        // Delete associated map footprints
        try {
            footprintService.deleteFootprintsByTravelRecord(recordId);
        } catch (Exception e) {
            // Log error but continue with record deletion
            System.err.println("Failed to delete map footprints for record " + recordId + ": " + e.getMessage());
        }

        // Delete from database (cascade delete will handle related data)
        travelRecordRepository.delete(record);
    }
}
