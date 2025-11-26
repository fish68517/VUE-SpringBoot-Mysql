package com.travelMemory.service;

import com.travelMemory.dto.CreateLikeRequest;
import com.travelMemory.dto.LikeResponse;
import com.travelMemory.entity.Like;
import com.travelMemory.entity.TravelRecord;
import com.travelMemory.repository.LikeRepository;
import com.travelMemory.repository.TravelRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final TravelRecordRepository travelRecordRepository;

    /**
     * Create a new like
     * @param userId the user ID
     * @param request the create like request
     * @return LikeResponse containing the created like
     * @throws IllegalArgumentException if validation fails
     */
    public LikeResponse createLike(Long userId, CreateLikeRequest request) {
        // Validate travel record exists
        TravelRecord travelRecord = travelRecordRepository.findById(request.getTravelRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found"));

        // Validate travel record is public or user is the owner
        if (!travelRecord.getIsPublic() && !travelRecord.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cannot like private records");
        }

        // Check if user has already liked this record
        if (likeRepository.existsByTravelRecordIdAndUserId(request.getTravelRecordId(), userId)) {
            throw new IllegalArgumentException("You have already liked this record");
        }

        // Create new like
        Like like = Like.builder()
                .travelRecordId(request.getTravelRecordId())
                .userId(userId)
                .build();

        // Save to database
        Like savedLike = likeRepository.save(like);

        // Convert to response DTO
        return LikeResponse.from(savedLike);
    }

    /**
     * Delete a like
     * @param likeId the like ID
     * @param userId the user ID (for ownership verification)
     * @throws IllegalArgumentException if like not found or user is not the owner
     */
    public void deleteLike(Long likeId, Long userId) {
        // Verify ownership
        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));

        if (!like.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: You can only delete your own likes");
        }

        // Delete from database
        likeRepository.delete(like);
    }

    /**
     * Delete a like by travel record ID and user ID
     * @param travelRecordId the travel record ID
     * @param userId the user ID
     * @throws IllegalArgumentException if like not found
     */
    public void deleteLikeByTravelRecordAndUser(Long travelRecordId, Long userId) {
        Like like = likeRepository.findByTravelRecordIdAndUserId(travelRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));

        likeRepository.delete(like);
    }

    /**
     * Get like count for a travel record
     * @param travelRecordId the travel record ID
     * @return the number of likes
     */
    @Transactional(readOnly = true)
    public long getLikeCount(Long travelRecordId) {
        return likeRepository.countByTravelRecordId(travelRecordId);
    }

    /**
     * Check if a user has liked a travel record
     * @param travelRecordId the travel record ID
     * @param userId the user ID
     * @return true if the user has liked the record, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long travelRecordId, Long userId) {
        return likeRepository.existsByTravelRecordIdAndUserId(travelRecordId, userId);
    }
}
