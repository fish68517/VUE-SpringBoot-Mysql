package com.health.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 健康建议请求DTO
 * 用于接收医师创建健康建议的请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAdviceRequest {

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 建议内容
     */
    private String adviceContent;

    /**
     * 推荐信息
     */
    private String recommendation;
}
