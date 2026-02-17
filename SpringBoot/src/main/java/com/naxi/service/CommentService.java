package com.naxi.service;

import com.naxi.common.BusinessException;
import com.naxi.entity.Comment;
import com.naxi.entity.Pattern;
import com.naxi.entity.User;
import com.naxi.repository.CommentRepository;
import com.naxi.repository.PatternRepository;
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

    @Autowired
    private PatternRepository patternRepository; // 必须注入 PatternRepository

    /**
     * 发布评论
     * 需求: 11.1, 11.2
     */
    public Comment publishComment(User user,Long userId, Long patternId, String content) {
        System.out.println("CommentService.publishComment - userId: " + userId);
        System.out.println("CommentService.publishComment - patternId: " + patternId);
        System.out.println("CommentService.publishComment - content: " + content);
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPatternId(patternId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setUser(user);

        // 关键修复点 2：根据 patternId 查找并设置 Pattern 对象
        // 只有设置了对象，JPA 才会把 pattern_id 写入数据库
        if (patternId != null) {
            Pattern pattern = patternRepository.findById(patternId)
                    .orElseThrow(() -> new BusinessException("该纹样不存在，无法发布评论"));
            comment.setPattern(pattern);
        }

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
