package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.AdminOverviewResponse;
import com.travelMemory.entity.UserRole;
import com.travelMemory.repository.CommentRepository;
import com.travelMemory.repository.LikeRepository;
import com.travelMemory.repository.TravelPlanRepository;
import com.travelMemory.repository.TravelRecordRepository;
import com.travelMemory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/overview")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final TravelRecordRepository travelRecordRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminOverviewResponse>> getOverview() {
        AdminOverviewResponse response = AdminOverviewResponse.builder()
                .totalUsers(userRepository.count())
                .totalAdmins(userRepository.countByRole(UserRole.ADMIN))
                .totalTravelRecords(travelRecordRepository.count())
                .totalTravelPlans(travelPlanRepository.count())
                .totalComments(commentRepository.count())
                .totalLikes(likeRepository.count())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
