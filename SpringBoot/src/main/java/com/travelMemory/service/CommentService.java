package com.travelMemory.service;

import com.travelMemory.dto.CommentResponse;
import com.travelMemory.dto.CreateCommentRequest;
import com.travelMemory.dto.UserInfo;
import com.travelMemory.entity.Comment;
import com.travelMemory.entity.TravelRecord;
import com.travelMemory.entity.User;
import com.travelMemory.repository.CommentRepository;
import com.travelMemory.repository.TravelRecordRepository;
import com.travelMemory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TravelRecordRepository travelRecordRepository;
    private final UserRepository userRepository;

    /**
     * Create a new comment
     * @param userId the user ID
     * @param request the create comment request
     * @return CommentResponse containing the created comment
     * @throws IllegalArgumentException if validation fails
     */
    public CommentResponse createComment(Long userId, CreateCommentRequest request) {
        // Validate travel record exists
        TravelRecord travelRecord = travelRecordRepository.findById(request.getTravelRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found"));

        // Validate travel record is public or user is the owner
        if (!travelRecord.getIsPublic() && !travelRecord.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cannot comment on private records");
        }

        // Validate content is not empty
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        // Create new comment
        Comment comment = Comment.builder()
                .travelRecordId(request.getTravelRecordId())
                .userId(userId)
                .content(request.getContent().trim())
                .build();

        // Save to database
        Comment savedComment = commentRepository.save(comment);

        // Convert to response DTO
        return CommentResponse.from(savedComment);
    }

    /**
     * Get comments for a travel record with pagination
     * @param travelRecordId the travel record ID
     * @param pageable pagination information
     * @return Page of comments
     * @throws IllegalArgumentException if travel record not found
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTravelRecord(Long travelRecordId, Pageable pageable) {
        // Validate travel record exists
        travelRecordRepository.findById(travelRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found"));

        Page<Comment> comments = commentRepository.findByTravelRecordId(travelRecordId, pageable);

        // Convert to response DTOs with user information
        List<CommentResponse> responses = comments.getContent().stream()
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElse(null);
                    UserInfo userInfo = user != null ? UserInfo.from(user) : null;
                    return CommentResponse.from(comment, userInfo);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    /**
     * Delete a comment
     * @param commentId the comment ID
     * @param userId the user ID (for ownership verification)
     * @throws IllegalArgumentException if comment not found or user is not the owner
     */
    public void deleteComment(Long commentId, Long userId) {
        // Verify ownership
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found or access denied"));

        // Delete from database
        commentRepository.delete(comment);
    }

    /**
     * Get comment count for a travel record
     * @param travelRecordId the travel record ID
     * @return the number of comments
     */
    @Transactional(readOnly = true)
    public long getCommentCount(Long travelRecordId) {
        return commentRepository.countByTravelRecordId(travelRecordId);
    }
}
