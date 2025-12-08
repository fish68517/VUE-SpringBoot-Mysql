package com.sharkfitness.controller;

import com.sharkfitness.dto.CreateFeedbackRequest;
import com.sharkfitness.dto.TrainingFeedbackDto;
import com.sharkfitness.service.TrainingFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
