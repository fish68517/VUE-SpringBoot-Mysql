package com.agricultural.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 * 
 * 提供应用程序健康状态检查接口
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    /**
     * 健康检查接口
     */
    @GetMapping
    public String health() {
        return "应用程序运行正常";
    }
}
