package com.health.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 咨询请求DTO
 * 用于接收用户提交咨询问题的请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequest {

    /**
     * 咨询问题
     */
    private String question;
}
