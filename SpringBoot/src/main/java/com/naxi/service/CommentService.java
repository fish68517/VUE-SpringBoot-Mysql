package com.naxi.service;

import com.naxi.entity.Comment;
import com.naxi.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    /**
     * 发布评论
     * 需求: 11.1, 11.2
     */
    public Comment publishComment(Long userId, Long patternId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPatternId(patternId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    /**
     * 获取纹样评论列表（支持分页）
     * 需求: 11.3, 11.4
     */
    public Page<Comment> getPatternComments(Long patternId, Pageable pageable) {
        return commentRepository.findByPatternId(patternId, pageable);
    }

    /**
     * 删除评论（管理员）
     * 需求: 11.2
     */
    public boolean deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }

    /**
     * 获取评论详情
     */
    public Comment getCommentById(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.orElse(null);
    }
}
