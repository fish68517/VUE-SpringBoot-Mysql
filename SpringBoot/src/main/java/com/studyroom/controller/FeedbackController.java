package com.studyroom.controller;

import com.studyroom.dto.Result;
import com.studyroom.entity.Feedback;
import com.studyroom.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/create")
    public Result<String> createFeedback(HttpServletRequest request, @RequestBody Feedback feedback) {
        Long userId = (Long) request.getAttribute("userId");
        return feedbackService.createFeedback(userId, feedback);
    }

    @GetMapping("/my")
    public Result<List<Map<String, Object>>> getMyFeedbacks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return feedbackService.getUserFeedbacks(userId);
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getAllFeedbacks(
            HttpServletRequest request,
            @RequestParam(required = false) Integer status) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return feedbackService.getAllFeedbacks(status);
    }

    @PostMapping("/reply/{id}")
    public Result<String> replyFeedback(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        Long userId = (Long) request.getAttribute("userId");
        return feedbackService.replyFeedback(id, body.get("reply"), userId);
    }

    @PutMapping("/status/{id}")
    public Result<String> updateFeedbackStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam Integer status) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return feedbackService.updateFeedbackStatus(id, status);
    }
}
