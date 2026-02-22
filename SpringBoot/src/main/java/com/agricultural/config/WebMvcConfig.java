package com.agricultural.config;

import com.agricultural.security.PermissionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 用于配置拦截器、消息转换器等
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private PermissionInterceptor permissionInterceptor;
    
    /**
     * 注册拦截器
     * 
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册权限拦截器
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/users/register",
                        "/api/users/login",
                        "/api/users/check/**",
                        "/api/weather/current",
                        "/api/weather/forecast",
                        "/api/weather/history",
                        "/api/products",
                        "/api/warnings"
                );
        
        log.info("权限拦截器已注册");
    }
}
