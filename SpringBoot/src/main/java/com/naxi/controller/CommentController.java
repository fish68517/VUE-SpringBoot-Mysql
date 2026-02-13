package com.naxi.controller;

import com.naxi.common.ApiResponse;
import com.naxi.entity.Comment;
import com.naxi.entity.Pattern;
import com.naxi.entity.User;
import com.naxi.service.CommentService;
import com.naxi.service.PatternService;
import com.naxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PatternService patternService;

    /**
     * 发布评论
     * 需求: 21.1
     */
    @PostMapping
    public ApiResponse<?> publishComment(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long patternId = ((Number) request.get("patternId")).longValue();
            String content = (String) request.get("content");

            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            if (patternId == null) {
                return ApiResponse.error(400, "纹样ID不能为空");
            }
            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.error(400, "评论内容不能为空");
            }

            // 验证用户是否存在
            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error(1001, "用户不存在");
            }

            // 验证纹样是否存在
            Pattern pattern = patternService.getPatternById(patternId);
            if (pattern == null) {
                return ApiResponse.error(2001, "纹样不存在");
            }

            Comment comment = commentService.publishComment(userId, patternId, content);
            return ApiResponse.success("评论发布成功", comment);
        } catch (Exception e) {
            return ApiResponse.error(500, "发布评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论列表
     * 需求: 21.1
     */
    @GetMapping
    public ApiResponse<?> getComments(
            @RequestParam Long patternId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (patternId == null) {
                return ApiResponse.error(400, "纹样ID不能为空");
            }

            // 验证纹样是否存在
            Pattern pattern = patternService.getPatternById(patternId);
            if (pattern == null) {
                return ApiResponse.error(2001, "纹样不存在");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> comments = commentService.getPatternComments(patternId, pageable);

            // 构建响应数据，包含用户信息
            List<Map<String, Object>> response = new ArrayList<>();
            for (Comment comment : comments.getContent()) {
                User user = userService.getUserById(comment.getUserId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", comment.getId());
                item.put("userId", comment.getUserId());
                item.put("patternId", comment.getPatternId());
                item.put("content", comment.getContent());
                item.put("createdAt", comment.getCreatedAt());
                item.put("updatedAt", comment.getUpdatedAt());
                if (user != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("nickname", user.getNickname());
                    userInfo.put("avatarUrl", user.getAvatarUrl());
                    item.put("user", userInfo);
                }
                response.add(item);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("content", response);
            result.put("totalElements", comments.getTotalElements());
            result.put("totalPages", comments.getTotalPages());
            result.put("currentPage", page);
            result.put("pageSize", size);

            return ApiResponse.success("获取评论列表成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论（管理员）
     * 需求: 21.1
     */
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteComment(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ApiResponse.error(400, "评论ID无效");
            }

            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                return ApiResponse.error(3001, "评论不存在");
            }

            boolean deleted = commentService.deleteComment(id);
            if (deleted) {
                return ApiResponse.success("删除评论成功");
            } else {
                return ApiResponse.error(500, "删除评论失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "删除评论失败: " + e.getMessage());
        }
    }
}
