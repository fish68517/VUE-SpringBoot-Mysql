package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    /**
     * 健康检查接口
     */
    @GetMapping
    public ApiResponse<String> health() {
        return ApiResponse.success("系统运行正常");
    }
}
