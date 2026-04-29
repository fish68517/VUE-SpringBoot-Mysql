package com.medical.internship.service;

import com.medical.internship.dto.HospitalStatisticsResponse;
import com.medical.internship.dto.SchoolStatisticsResponse;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取学校数据统计
     * 返回申请人数、通过人数、实习中人数、完成率等
     */
    SchoolStatisticsResponse getSchoolStatistics();
    
    /**
     * 获取医院数据统计
     * 返回岗位发布数、投递数、录用数等
     */
    HospitalStatisticsResponse getHospitalStatistics();
}
