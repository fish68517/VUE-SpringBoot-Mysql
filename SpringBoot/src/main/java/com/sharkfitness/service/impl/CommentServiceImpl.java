package com.sharkfitness.service.impl;

import com.sharkfitness.dto.CommentRequest;
import com.sharkfitness.entity.Comment;
import com.sharkfitness.entity.Dynamic;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.exception.UnauthorizedException;
import com.sharkfitness.repository.CommentRepository;
import com.sharkfitness.repository.DynamicRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.CommentService;
import com.sharkfitness.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of comment service
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    private final DynamicRepository dynamicRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public CommentVO create(Long userId, CommentRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Dynamic dynamic = dynamicRepository.findById(request.getDynamicId())
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setDynamic(dynamic);
        
        comment = commentRepository.save(comment);
        
        // Update comment count
        dynamic.setCommentCount(dynamic.getCommentCount() + 1);
        dynamicRepository.save(dynamic);
        
        return convertToVO(comment);
    }
    
    @Override
    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        // Check if user is the owner of the comment
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }
        
        Dynamic dynamic = comment.getDynamic();
        
        commentRepository.delete(comment);
        
        // Update comment count
        dynamic.setCommentCount(Math.max(0, dynamic.getCommentCount() - 1));
        dynamicRepository.save(dynamic);
    }
    
    @Override
    public List<CommentVO> findByDynamic(Long dynamicId) {
        // Check if dynamic exists
        if (!dynamicRepository.existsById(dynamicId)) {
            throw new ResourceNotFoundException("Dynamic post not found");
        }
        
        List<Comment> comments = commentRepository.findByDynamicIdOrderByCreatedAtAsc(dynamicId);
        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Comment entity to CommentVO
     */
    private CommentVO convertToVO(Comment comment) {
        return new CommentVO(
            comment.getId(),
            comment.getContent(),
            comment.getUser().getId(),
            comment.getUser().getUsername(),
            comment.getUser().getAvatar(),
            comment.getDynamic().getId(),
            comment.getCreatedAt()
        );
    }
}
