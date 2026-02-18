package com.medical.internship.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 注册拦截器和Bean
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private SessionInterceptor sessionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 修改前
    /*    registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/users/login",
                        "/api/users/register",
                        "/health"
                );*/
    /*    registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/**" // 👈 新增这一行：放行所有请求
                );*/

        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/users/login",       // 放行登录
                        "/api/users/register",    // 放行注册
                        "/api/organizations",     // 放行获取组织列表（供注册下拉框使用）
                        "/health"                 // 放行健康检查
                );
    }

}

