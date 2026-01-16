package com.postgraduate.service;

import com.postgraduate.dto.CommentDTO;
import com.postgraduate.entity.Comment;
import com.postgraduate.entity.School;
import com.postgraduate.entity.User;
import com.postgraduate.exception.AuthorizationException;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.repository.CommentRepository;
import com.postgraduate.repository.SchoolRepository;
import com.postgraduate.repository.UserRepository;
import com.postgraduate.util.AuthorizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling comment management operations.
 * Provides methods for creating, retrieving, and managing comments and replies.
 */
@Slf4j
@Service
public class CommentService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    /**
     * Get all comments for a specific school with pagination.
     * Only returns comments with NORMAL status (excludes deleted comments).
     *
     * @param schoolId the school ID
     * @param page page number (0-indexed)
     * @param size page size (default 20 if not specified or exceeds max)
     * @return Page of CommentDTO containing school's comments
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional(readOnly = true)
    public Page<CommentDTO> getSchoolComments(Long schoolId, int page, Integer size) {
        log.info("Retrieving comments for school id: {} - page: {}, size: {}", schoolId, page, size);

        // Verify school exists
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Validate and set page size
        int pageSize = (size == null || size <= 0 || size > 100) ? DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Comment> comments = commentRepository.findBySchoolIdAndStatusAndDeletedFalse(
                schoolId, Comment.CommentStatus.NORMAL, pageable);
        log.info("Found {} comments for school {}", comments.getTotalElements(), schoolId);

        return comments.map(CommentDTO::fromEntity);
    }

    /**
     * Create a new comment for a school.
     *
     * @param schoolId the school ID
     * @param content the comment content
     * @return CommentDTO containing the created comment information
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional
    public CommentDTO createComment(Long schoolId, String content) {
        log.info("Creating comment for school id: {}", schoolId);

        User currentUser = authorizationUtil.getCurrentUser();

        // Verify school exists
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Create new comment
        Comment comment = Comment.builder()
                .schoolId(schoolId)
                .userId(currentUser.getId())
                .content(content)
                .status(Comment.CommentStatus.NORMAL)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {} for school {}", savedComment.getId(), schoolId);

        return CommentDTO.fromEntityWithAuthor(savedComment, currentUser.getUsername());
    }

    /**
     * Create a reply to an existing comment.
     *
     * @param parentCommentId the parent comment ID
     * @param content the reply content
     * @return CommentDTO containing the created reply information
     * @throws ResourceNotFoundException if parent comment is not found
     */
    @Transactional
    public CommentDTO createReply(Long parentCommentId, String content) {
        log.info("Creating reply to comment id: {}", parentCommentId);

        User currentUser = authorizationUtil.getCurrentUser();

        // Verify parent comment exists
        Comment parentComment = commentRepository.findByIdAndDeletedFalse(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + parentCommentId));

        // Create reply comment
        Comment reply = Comment.builder()
                .schoolId(parentComment.getSchoolId())
                .userId(currentUser.getId())
                .parentId(parentCommentId)
                .content(content)
                .status(Comment.CommentStatus.NORMAL)
                .build();

        Comment savedReply = commentRepository.save(reply);
        log.info("Reply created successfully with id: {} for parent comment {}", savedReply.getId(), parentCommentId);

        return CommentDTO.fromEntityWithAuthor(savedReply, currentUser.getUsername());
    }

    /**
     * Delete a comment by marking it as DELETED.
     * Uses soft delete to maintain data integrity.
     *
     * @param commentId the comment ID to delete
     * @throws ResourceNotFoundException if comment is not found
     */
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Deleting comment id: {}", commentId);

        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        comment.setStatus(Comment.CommentStatus.DELETED);
        comment.setDeleted(true);
        commentRepository.save(comment);
        log.info("Comment deleted successfully with id: {}", commentId);
    }

    /**
     * Delete a comment with authorization check.
     * Only the comment author or an admin can delete a comment.
     *
     * @param commentId the comment ID to delete
     * @throws ResourceNotFoundException if comment is not found
     * @throws AuthorizationException if user is not authorized to delete the comment
     */
    @Transactional
    public void deleteCommentWithAuthorization(Long commentId) {
        log.info("Deleting comment id: {} with authorization check", commentId);

        User currentUser = authorizationUtil.getCurrentUser();
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the comment author or an admin
        boolean isAuthor = comment.getUserId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.UserRole.ADMIN;

        if (!isAuthor && !isAdmin) {
            log.warn("User {} attempted to delete comment {} they don't own", currentUser.getId(), commentId);
            throw new AuthorizationException("You are not authorized to delete this comment");
        }

        comment.setStatus(Comment.CommentStatus.DELETED);
        comment.setDeleted(true);
        commentRepository.save(comment);
        log.info("Comment deleted successfully with id: {} by user {}", commentId, currentUser.getId());
    }
}
