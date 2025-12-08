package com.sharkfitness.service;

import com.sharkfitness.dto.CreateFeedbackRequest;
import com.sharkfitness.dto.TrainingFeedbackDto;
import com.sharkfitness.entity.TrainingFeedback;
import com.sharkfitness.repository.TrainingFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok: creates a constructor with all final fields
public class TrainingFeedbackService {

    private final TrainingFeedbackRepository feedbackRepository;

    /**
     * Get all feedback for a specific training plan.
     */
    @Transactional(readOnly = true)
    public List<TrainingFeedbackDto> getFeedbackByPlanId(Long planId) {
        List<TrainingFeedback> feedbacks = feedbackRepository.findByPlanIdOrderByFeedbackDateDesc(planId);
        return feedbacks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new feedback entry.
     * The studentId should be retrieved from the security context.
     */
    @Transactional
    public TrainingFeedbackDto createFeedback(CreateFeedbackRequest request, Long studentId) {
        TrainingFeedback feedback = new TrainingFeedback();
        feedback.setPlanId(request.getPlanId());
        feedback.setStudentId(studentId); // Set student ID from authenticated user
        feedback.setContent(request.getContent());
        feedback.setRating(request.getRating());
        feedback.setFeedbackDate(request.getFeedbackDate());

        TrainingFeedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDto(savedFeedback);
    }

    /**
     * Helper method to convert Entity to DTO.
     */
    private TrainingFeedbackDto convertToDto(TrainingFeedback feedback) {
        return TrainingFeedbackDto.builder()
                .id(feedback.getId())
                .planId(feedback.getPlanId())
                .content(feedback.getContent())
                .rating(feedback.getRating())
                .feedbackDate(feedback.getFeedbackDate())
                .build();
    }
}
