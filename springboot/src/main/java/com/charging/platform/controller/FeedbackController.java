package com.charging.platform.controller;

import com.charging.platform.common.PageResult;
import com.charging.platform.common.Result;
import com.charging.platform.dto.FeedbackRequest;
import com.charging.platform.entity.UserFeedback;
import com.charging.platform.service.FeedbackService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public Result<PageResult<UserFeedback>> page(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") long pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页条数必须大于等于1")
            @Max(value = 100, message = "每页最多查询100条") long pageSize) {
        return Result.success(feedbackService.getFeedbacks(userId, pageNum, pageSize));
    }

    @PostMapping
    public Result<UserFeedback> submit(@Valid @RequestBody FeedbackRequest request) {
        return Result.success(feedbackService.submit(request));
    }
}
