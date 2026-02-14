package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.CommentCreateRequest;
import com.zhuang.embroidery.dto.CommentListResponse;
import com.zhuang.embroidery.dto.CommentResponse;
import com.zhuang.embroidery.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论相关 API 控制器
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取评论列表（按作品或话题）
     *
     * @param artworkId 作品ID（可选）
     * @param topicId 话题ID（可选）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 评论列表响应
     */
    @GetMapping
    public ApiResponse<CommentListResponse> getCommentList(
            @RequestParam(required = false) Long artworkId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取评论列表: artworkId={}, topicId={}, pageNum={}, pageSize={}", artworkId, topicId, pageNum, pageSize);

        try {
            CommentListResponse response = commentService.getCommentList(artworkId, topicId, pageNum, pageSize);
            log.info("成功获取评论列表，共 {} 条", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取评论列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取评论列表异常", e);
            return ApiResponse.serverError("获取评论列表失败");
        }
    }

    /**
     * 发布评论
     *
     * @param request 评论创建请求
     * @return 评论响应
     */
    @PostMapping
    public ApiResponse<CommentResponse> createComment(@RequestBody CommentCreateRequest request) {
        log.info("发布评论: userId={}, artworkId={}, topicId={}", request.getUserId(), request.getArtworkId(), request.getTopicId());

        try {
            CommentResponse response = commentService.createComment(request);
            log.info("评论发布成功: commentId={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("发布评论失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("发布评论异常", e);
            return ApiResponse.serverError("发布评论失败");
        }
    }

    /**
     * 删除评论（管理员）
     *
     * @param id 评论ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        log.info("删除评论: id={}", id);

        try {
            commentService.deleteComment(id);
            log.info("评论删除成功: id={}", id);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("删除评论失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("删除评论异常", e);
            return ApiResponse.serverError("删除评论失败");
        }
    }

    /**
     * 回复评论（管理员）
     *
     * @param request 评论创建请求（parentId 应该被设置）
     * @return 评论响应
     */
    @PostMapping("/reply")
    public ApiResponse<CommentResponse> replyComment(@RequestBody CommentCreateRequest request) {
        log.info("回复评论: userId={}, parentId={}", request.getUserId(), request.getParentId());

        try {
            CommentResponse response = commentService.replyComment(request);
            log.info("评论回复成功: replyId={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("回复评论失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("回复评论异常", e);
            return ApiResponse.serverError("回复评论失败");
        }
    }

    /**
     * 获取评论的所有回复
     *
     * @param commentId 评论ID
     * @return 回复列表响应
     */
    @GetMapping("/{commentId}/replies")
    public ApiResponse<List<CommentResponse>> getCommentReplies(@PathVariable Long commentId) {
        log.info("获取评论回复: commentId={}", commentId);

        try {
            List<CommentResponse> replies = commentService.getCommentReplies(commentId);
            log.info("成功获取评论回复，共 {} 条", replies.size());
            return ApiResponse.success(replies);
        } catch (IllegalArgumentException e) {
            log.warn("获取评论回复失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取评论回复异常", e);
            return ApiResponse.serverError("获取评论回复失败");
        }
    }

}
