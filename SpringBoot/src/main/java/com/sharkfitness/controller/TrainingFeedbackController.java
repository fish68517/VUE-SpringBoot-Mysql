package com.sharkfitness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sharkfitness.dto.CreateFeedbackRequest;
import com.sharkfitness.dto.TrainingFeedbackDto;
import com.sharkfitness.entity.TrainingFeedback;
import com.sharkfitness.service.TrainingFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/training-feedback")
@RequiredArgsConstructor
public class TrainingFeedbackController {

    private final TrainingFeedbackService feedbackService;

    /**
     * GET /api/training-feedback?planId={id}
     * Get feedback history for a specific training plan.
     */
    @GetMapping
    public ResponseEntity<List<TrainingFeedbackDto>> getFeedbackForPlan(@RequestParam Long planId) {
        List<TrainingFeedbackDto> feedbackList = feedbackService.getFeedbackByPlanId(planId);
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * POST /api/training-feedback
     * Create a new feedback entry.
     */
    @PostMapping
    public ResponseEntity<TrainingFeedbackDto> createFeedback(@Valid @RequestBody CreateFeedbackRequest request) {


        TrainingFeedbackDto createdFeedback = feedbackService.createFeedback(request, request.getStudentId());
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }

    // 通过 coachId 和 studentId 获取反馈列表
    /**
     * 获取学员反馈列表
     * URL: /training-feedback/list?student_id=10&coach_id=3
     */
    @GetMapping("/list")
    public ResponseEntity<List<TrainingFeedback>> getList(
            @RequestParam("student_id") Long studentId,
            @RequestParam("coach_id") Long coachId
    ) {
        // 使用 MyBatis-Plus 构建查询条件
        LambdaQueryWrapper<TrainingFeedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingFeedback::getStudentId, studentId)
                .eq(TrainingFeedback::getCoachId, coachId)
                // 按反馈日期倒序排列（最新的在最上面）
                .orderByDesc(TrainingFeedback::getFeedbackDate)
                .orderByDesc(TrainingFeedback::getCreatedAt);

        List<TrainingFeedback> list = feedbackService.list(studentId, coachId);
        return ResponseEntity.ok(list);
    }

    /**
     * 删除反馈
     * 对应前端 api: /training/feedback/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteFeedback(@PathVariable Long id) {
        feedbackService.removeById(id);
        return ResponseEntity.ok(true);
    }

    /**
     * 教练回复反馈
     * 对应前端 api: /training/feedback/reply (注意路径统一)
     */
    @PostMapping("/reply")
    public ResponseEntity<Boolean> replyFeedback(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String content = (String) params.get("coach_reply");

        TrainingFeedback feedback = new TrainingFeedback();
        feedback.setId(id);
        feedback.setCoachReply(content);
        feedback.setReplyAt(LocalDate.from(LocalDateTime.now()));

        feedbackService.updateById(feedback);
        return ResponseEntity.ok(true);
    }
}
