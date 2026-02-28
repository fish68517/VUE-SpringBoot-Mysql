package com.health.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 咨询回复请求DTO
 * 用于接收医师回复咨询问题的请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationAnswerRequest {

    /**
     * 回复内容
     */
    private String answer;
}
