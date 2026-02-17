package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.*;
import com.zhuang.embroidery.service.CommentService;
import com.zhuang.embroidery.service.FeedbackService;
import com.zhuang.embroidery.service.TopicService;
import com.zhuang.embroidery.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台互动管理 API 控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminInteractionController {

    private final CommentService commentService;
    private final TopicService topicService;
    private final VoteService voteService;
    private final FeedbackService feedbackService;

    // ==================== 评论管理接口 ====================

    /**
     * 获取评论列表
     *
     * @param artworkId 作品ID（可选）
     * @param topicId 话题ID（可选）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 评论列表响应
     */
    @GetMapping("/comments")
    public ApiResponse<CommentListResponse> getCommentList(
            @RequestParam(required = false) Long artworkId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取评论列表: artworkId={}, topicId={}, pageNum={}, pageSize={}", artworkId, topicId, pageNum, pageSize);

        try {
            CommentListResponse response = commentService.getCommentList(artworkId, topicId, pageNum, pageSize);
            log.info("成功获取评论列表: total={}", response.getTotal());
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
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 成功响应
     */
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        log.info("删除评论: commentId={}", commentId);

        try {
            commentService.deleteComment(commentId);
            log.info("评论删除成功: commentId={}", commentId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("评论删除失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("评论删除异常", e);
            return ApiResponse.serverError("评论删除失败");
        }
    }


    /**
     * 处理反馈（将待处理标记为已处理）
     *
     * @param feedbackId 反馈ID
     * @return 成功响应
     */
    @PostMapping("/feedback/{feedbackId}/process")
    public ApiResponse<Void> processFeedback(@PathVariable Long feedbackId) {
        log.info("处理用户反馈: feedbackId={}", feedbackId);

        try {
            // 调用 service 层的处理逻辑
            feedbackService.processFeedback(feedbackId);
            log.info("反馈处理成功: feedbackId={}", feedbackId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("反馈处理失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("反馈处理异常", e);
            return ApiResponse.serverError("反馈处理失败");
        }
    }

    /**
     * 回复评论
     *
     * @param commentId 被回复的评论ID
     * @param request 评论创建请求
     * @return 评论响应
     */
    @PostMapping("/comments/{commentId}/reply")
    public ApiResponse<CommentResponse> replyComment(
            @PathVariable Long commentId,
            @RequestBody CommentCreateRequest request) {
        log.info("回复评论: commentId={}, userId={}", commentId, request.getUserId());

        try {
            // 设置父评论ID
            request.setParentId(commentId);
            CommentResponse response = commentService.replyComment(request);
            log.info("评论回复成功: replyId={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("评论回复失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("评论回复异常", e);
            return ApiResponse.serverError("评论回复失败");
        }
    }

    // ==================== 话题管理接口 ====================

    /**
     * 获取话题列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 话题列表响应
     */
    @GetMapping("/topics")
    public ApiResponse<TopicListResponse> getTopicList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取话题列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            TopicListResponse response = topicService.getTopicList(pageNum, pageSize);
            log.info("成功获取话题列表: total={}", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取话题列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取话题列表异常", e);
            return ApiResponse.serverError("获取话题列表失败");
        }
    }

    /**
     * 置顶话题
     *
     * @param topicId 话题ID
     * @return 话题响应
     */
    @PostMapping("/topics/{topicId}/pin")
    public ApiResponse<TopicResponse> pinTopic(@PathVariable Long topicId) {
        log.info("置顶话题: topicId={}", topicId);

        try {
            TopicResponse response = topicService.pinTopic(topicId);
            log.info("话题置顶成功: topicId={}", topicId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("话题置顶失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("话题置顶异常", e);
            return ApiResponse.serverError("话题置顶失败");
        }
    }

    /**
     * 删除话题
     *
     * @param topicId 话题ID
     * @return 成功响应
     */
    @DeleteMapping("/topics/{topicId}")
    public ApiResponse<Void> deleteTopic(@PathVariable Long topicId) {
        log.info("删除话题: topicId={}", topicId);

        try {
            topicService.deleteTopic(topicId);
            log.info("话题删除成功: topicId={}", topicId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("话题删除失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("话题删除异常", e);
            return ApiResponse.serverError("话题删除失败");
        }
    }

    // ==================== 投票统计接口 ====================

    /**
     * 获取投票统计
     *
     * @param voteId 投票ID
     * @return 投票统计响应
     */
    @GetMapping("/votes/{voteId}/statistics")
    public ApiResponse<VoteStatisticsResponse> getVoteStatistics(@PathVariable Long voteId) {
        log.info("获取投票统计: voteId={}", voteId);

        try {
            VoteStatisticsResponse response = voteService.getVoteStatistics(voteId);
            log.info("成功获取投票统计: voteId={}, totalVotes={}", voteId, response.getTotalVotes());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取投票统计失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取投票统计异常", e);
            return ApiResponse.serverError("获取投票统计失败");
        }
    }

    // ==================== 反馈管理接口 ====================

    //     GEThttp://localhost:8080/api/admin/votes?pageNum=1&pageSize=10



    /**
     * 获取投票列表 (分页)
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 投票列表响应
     */
    @GetMapping("/votes")
    public ApiResponse<VoteListResponse> getVoteList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取投票列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            VoteListResponse response = voteService.getVoteList(pageNum, pageSize);
            log.info("成功获取投票列表: total={}", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取投票列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取投票列表异常", e);
            return ApiResponse.serverError("获取投票列表失败");
        }
    }



    /**
     * 获取反馈列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 反馈列表响应
     */
    @GetMapping("/feedback")
    public ApiResponse<FeedbackListResponse> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取反馈列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            FeedbackListResponse response = feedbackService.getFeedbackList(pageNum, pageSize);
            log.info("成功获取反馈列表: total={}", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取反馈列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取反馈列表异常", e);
            return ApiResponse.serverError("获取反馈列表失败");
        }
    }

}
