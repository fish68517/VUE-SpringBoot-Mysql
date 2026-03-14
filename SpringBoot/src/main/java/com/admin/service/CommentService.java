package com.admin.service;

import com.admin.dto.CommentDTO;
import com.admin.entity.Comment;
import com.admin.mapper.CommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public CommentDTO getCommentById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }

        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return null;
        }

        return convertToDTO(comment);
    }

    public CommentDTO createComment(Comment comment) {
        if (comment == null || comment.getPostId() == null || comment.getPostId() <= 0
                || comment.getUserId() == null || comment.getUserId() <= 0) {
            return null;
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return null;
        }

        comment.setContent(comment.getContent().trim());
        int result = commentMapper.insert(comment);
        if (result <= 0 || comment.getId() == null) {
            return null;
        }
        return getCommentById(comment.getId());
    }

    public boolean deleteComment(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        Comment existingComment = commentMapper.findById(id);
        if (existingComment == null) {
            return false;
        }

        return commentMapper.deleteById(id) > 0;
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}
