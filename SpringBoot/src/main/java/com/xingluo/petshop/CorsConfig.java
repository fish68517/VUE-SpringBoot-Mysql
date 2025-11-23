package com.xingluo.petshop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 * 允许所有来源、所有请求头、所有方法，并允许携带 Cookie
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求路径
        registry.addMapping("/**")
                // 允许所有来源 (使用 allowedOriginPatterns 配合 allowCredentials)
                .allowedOriginPatterns("*")
                // 允许所有 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 允许携带凭证（如 Cookie, Authorization 头）
                .allowCredentials(true)
                // 预检请求的有效期，单位秒
                .maxAge(3600);
    }
}