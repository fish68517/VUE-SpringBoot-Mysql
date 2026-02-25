package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Comment;
import com.tourism.service.CommentService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 留言管理控制器
 */
@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {
    
    private static final Logger logger = LoggerUtil.getLogger(CommentController.class);
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 创建留言端点
     * @param request 创建留言请求体
     * @return API响应
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> createComment(@RequestBody CreateCommentRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建留言请求 - 用户ID: " + request.getUserId() + 
                ", 目标类型: " + request.getTargetType() + ", 目标ID: " + request.getTargetId());
            
            Comment comment = commentService.createComment(
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId(),
                request.getContent(),
                request.getRating()
            );
            
            Map<String, Object> response = buildCommentResponse(comment);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建留言失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取指定目标的留言列表端点
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/target/{targetType}/{targetId}")
    public ApiResponse<Map<String, Object>> getCommentsByTarget(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取留言列表请求 - 目标类型: " + targetType + 
                ", 目标ID: " + targetId + ", 页码: " + page + ", 每页数量: " + size);
            
            Page<Comment> commentPage = commentService.getCommentsByTarget(targetType, targetId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comments", commentPage.getContent().stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList()));
            response.put("total", commentPage.getTotalElements());
            response.put("currentPage", commentPage.getNumber());
            response.put("pageSize", commentPage.getSize());
            response.put("totalPages", commentPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取留言列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的留言列表端点
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<Map<String, Object>> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取用户留言列表请求 - 用户ID: " + userId + 
                ", 页码: " + page + ", 每页数量: " + size);
            
            Page<Comment> commentPage = commentService.getCommentsByUser(userId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comments", commentPage.getContent().stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList()));
            response.put("total", commentPage.getTotalElements());
            response.put("currentPage", commentPage.getNumber());
            response.put("pageSize", commentPage.getSize());
            response.put("totalPages", commentPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取用户留言列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取指定目标的置顶留言端点
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return API响应
     */
    @GetMapping("/pinned/{targetType}/{targetId}")
    public ApiResponse<Map<String, Object>> getPinnedComments(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        try {
            LoggerUtil.info(logger, "处理获取置顶留言请求 - 目标类型: " + targetType + ", 目标ID: " + targetId);
            
            List<Comment> comments = commentService.getPinnedComments(targetType, targetId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comments", comments.stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList()));
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取置顶留言失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批准留言端点（管理员）
     * @param id 留言ID
     * @return API响应
     */
    @PutMapping("/admin/{id}/approve")
    public ApiResponse<Map<String, Object>> approveComment(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理批准留言请求 - ID: " + id);
            
            Comment comment = commentService.approveComment(id);
            
            Map<String, Object> response = buildCommentResponse(comment);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "批准留言失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 拒绝留言端点（管理员）
     * @param id 留言ID
     * @return API响应
     */
    @PutMapping("/admin/{id}/reject")
    public ApiResponse<Map<String, Object>> rejectComment(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理拒绝留言请求 - ID: " + id);
            
            Comment comment = commentService.rejectComment(id);
            
            Map<String, Object> response = buildCommentResponse(comment);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "拒绝留言失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 置顶留言端点（管理员）
     * @param id 留言ID
     * @return API响应
     */
    @PutMapping("/admin/{id}/pin")
    public ApiResponse<Map<String, Object>> pinComment(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理置顶留言请求 - ID: " + id);
            
            Comment comment = commentService.pinComment(id);
            
            Map<String, Object> response = buildCommentResponse(comment);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "置顶留言失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 取消置顶留言端点（管理员）
     * @param id 留言ID
     * @return API响应
     */
    @PutMapping("/admin/{id}/unpin")
    public ApiResponse<Map<String, Object>> unpinComment(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理取消置顶留言请求 - ID: " + id);
            
            Comment comment = commentService.unpinComment(id);
            
            Map<String, Object> response = buildCommentResponse(comment);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "取消置顶留言失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除留言端点（管理员）
     * @param id 留言ID
     * @return API响应
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteComment(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理删除留言请求 - ID: " + id);
            
            commentService.deleteComment(id);
            
            return ApiResponse.success("留言删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除留言失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取待审核留言列表端点（管理员）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/admin/pending")
    public ApiResponse<Map<String, Object>> getPendingComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取待审核留言列表请求 - 页码: " + page + ", 每页数量: " + size);
            
            Page<Comment> commentPage = commentService.getPendingComments(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comments", commentPage.getContent().stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList()));
            response.put("total", commentPage.getTotalElements());
            response.put("currentPage", commentPage.getNumber());
            response.put("pageSize", commentPage.getSize());
            response.put("totalPages", commentPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取待审核留言列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建留言响应对象
     * @param comment 留言对象
     * @return 留言响应Map
     */
    private Map<String, Object> buildCommentResponse(Comment comment) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", comment.getId());
        response.put("userId", comment.getUserId());
        response.put("targetType", comment.getTargetType());
        response.put("targetId", comment.getTargetId());
        response.put("content", comment.getContent());
        response.put("rating", comment.getRating());
        response.put("status", comment.getStatus());
        response.put("isPinned", comment.getIsPinned());
        response.put("createdAt", comment.getCreatedAt());
        response.put("updatedAt", comment.getUpdatedAt());
        return response;
    }
    
    /**
     * 创建留言请求体
     */
    public static class CreateCommentRequest {
        private Long userId;
        private String targetType;
        private Long targetId;
        private String content;
        private Integer rating;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getTargetType() {
            return targetType;
        }
        
        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }
        
        public Long getTargetId() {
            return targetId;
        }
        
        public void setTargetId(Long targetId) {
            this.targetId = targetId;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public Integer getRating() {
            return rating;
        }
        
        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
}
