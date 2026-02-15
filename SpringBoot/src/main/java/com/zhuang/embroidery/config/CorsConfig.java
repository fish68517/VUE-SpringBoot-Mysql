package com.zhuang.embroidery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")               // 所有接口
                .allowedOriginPatterns("*")      // 允许所有来源（比 allowedOrigins("*") 更兼容）
                .allowedMethods("*")             // 允许所有方法 GET/POST/PUT/DELETE...
                .allowedHeaders("*")             // 允许所有请求头
                .exposedHeaders("*")             // 暴露所有响应头（可选）
                .allowCredentials(false)         // 最简单：不带 Cookie
                .maxAge(3600);                   // 预检请求缓存 1 小时
    }
}
