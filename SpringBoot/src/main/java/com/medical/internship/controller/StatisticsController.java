package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.dto.HospitalStatisticsResponse;
import com.medical.internship.dto.SchoolStatisticsResponse;
import com.medical.internship.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * 获取学校数据统计
     * 返回申请人数、通过人数、实习中人数、完成率等
     */
    @GetMapping("/school")
    public ResponseEntity<ApiResponse<SchoolStatisticsResponse>> getSchoolStatistics() {
        SchoolStatisticsResponse statistics = statisticsService.getSchoolStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * 获取医院数据统计
     * 返回岗位发布数、投递数、录用数等
     */
    @GetMapping("/hospital")
    public ResponseEntity<ApiResponse<HospitalStatisticsResponse>> getHospitalStatistics() {
        HospitalStatisticsResponse statistics = statisticsService.getHospitalStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}
