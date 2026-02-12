package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.dto.HospitalStatisticsResponse;
import com.medical.internship.dto.SchoolStatisticsResponse;
import com.medical.internship.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 数据统计控制器测试类
 */
@ExtendWith(MockitoExtension.class)
public class StatisticsControllerTest {
    
    @Mock
    private StatisticsService statisticsService;
    
    @InjectMocks
    private StatisticsController statisticsController;
    
    @Test
    public void testGetSchoolStatisticsSuccess() {
        // 准备测试数据
        SchoolStatisticsResponse mockResponse = SchoolStatisticsResponse.builder()
                .totalApplications(10L)
                .approvedApplications(8L)
                .ongoingInternships(5L)
                .completedInternships(3L)
                .completionRate(37.5)
                .rejectedApplications(2L)
                .build();
        
        // 模拟service行为
        when(statisticsService.getSchoolStatistics()).thenReturn(mockResponse);
        
        // 执行测试
        ResponseEntity<ApiResponse<SchoolStatisticsResponse>> response = 
                statisticsController.getSchoolStatistics();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("操作成功", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(10L, response.getBody().getData().getTotalApplications());
        assertEquals(8L, response.getBody().getData().getApprovedApplications());
        
        // 验证方法调用
        verify(statisticsService, times(1)).getSchoolStatistics();
    }
    
    @Test
    public void testGetHospitalStatisticsSuccess() {
        // 准备测试数据
        HospitalStatisticsResponse mockResponse = HospitalStatisticsResponse.builder()
                .totalPosts(5L)
                .totalApplications(20L)
                .approvedApplications(15L)
                .ongoingInternships(10L)
                .completedInternships(5L)
                .rejectedApplications(5L)
                .build();
        
        // 模拟service行为
        when(statisticsService.getHospitalStatistics()).thenReturn(mockResponse);
        
        // 执行测试
        ResponseEntity<ApiResponse<HospitalStatisticsResponse>> response = 
                statisticsController.getHospitalStatistics();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("操作成功", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(5L, response.getBody().getData().getTotalPosts());
        assertEquals(20L, response.getBody().getData().getTotalApplications());
        
        // 验证方法调用
        verify(statisticsService, times(1)).getHospitalStatistics();
    }
}
