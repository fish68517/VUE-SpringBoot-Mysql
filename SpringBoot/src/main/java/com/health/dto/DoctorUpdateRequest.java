package com.health.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医师信息更新请求DTO
 * 用于接收医师信息更新的请求数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateRequest {

    /**
     * 专科
     */
    private String specialization;

    /**
     * 医院
     */
    private String hospital;
}
