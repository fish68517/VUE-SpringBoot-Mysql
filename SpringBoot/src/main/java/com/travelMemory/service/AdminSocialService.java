package com.travelMemory.service;

import com.travelMemory.dto.CommentResponse;
import com.travelMemory.dto.LikeResponse;
import com.travelMemory.dto.UserInfo;
import com.travelMemory.entity.Comment;
import com.travelMemory.entity.Like;
import com.travelMemory.entity.User;
import com.travelMemory.repository.CommentRepository;
import com.travelMemory.repository.LikeRepository;
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
public class AdminSocialService {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Pageable pageable) {
        Page<Comment> comments = commentRepository.findAll(pageable);
        List<CommentResponse> items = comments.getContent().stream()
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElse(null);
                    UserInfo userInfo = user != null ? UserInfo.from(user) : null;
                    return CommentResponse.from(comment, userInfo);
                })
                .collect(Collectors.toList());
        return new PageImpl<>(items, pageable, comments.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<LikeResponse> getLikes(Pageable pageable) {
        return likeRepository.findAll(pageable).map(LikeResponse::from);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        commentRepository.delete(comment);
    }

    public void deleteLike(Long likeId) {
        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new IllegalArgumentException("点赞记录不存在"));
        likeRepository.delete(like);
    }
}
