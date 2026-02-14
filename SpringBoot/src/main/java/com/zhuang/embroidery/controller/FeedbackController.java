package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.FeedbackCreateRequest;
import com.zhuang.embroidery.dto.FeedbackListResponse;
import com.zhuang.embroidery.dto.FeedbackResponse;
import com.zhuang.embroidery.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈相关 API 控制器
 */
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 提交反馈
     *
     * @param request 反馈创建请求
     * @return 反馈响应
     */
    @PostMapping
    public ApiResponse<FeedbackResponse> submitFeedback(@RequestBody FeedbackCreateRequest request) {
        log.info("提交反馈: userId={}", request.getUserId());

        try {
            FeedbackResponse response = feedbackService.submitFeedback(request);
            log.info("反馈提交成功: feedbackId={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("提交反馈失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("提交反馈异常", e);
            return ApiResponse.serverError("提交反馈失败");
        }
    }

    /**
     * 获取反馈列表（管理员）
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 反馈列表响应
     */
    @GetMapping
    public ApiResponse<FeedbackListResponse> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取反馈列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            FeedbackListResponse response = feedbackService.getFeedbackList(pageNum, pageSize);
            log.info("成功获取反馈列表，共 {} 条", response.getTotal());
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
