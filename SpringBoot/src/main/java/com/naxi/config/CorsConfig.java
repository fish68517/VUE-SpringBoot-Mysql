package com.naxi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 1. 使用 setAllowedOriginPatterns 而不是 addAllowedOrigin
        // 这能更好地兼容 setAllowCredentials(true)，并支持通配符
        config.setAllowedOriginPatterns(Collections.singletonList("*"));

        // 2. 允许所有的 HTTP 方法
        config.addAllowedMethod("*");

        // 3. 允许所有的请求头
        config.addAllowedHeader("*");

        // 4. 必须允许发送 Cookie (如果你前端有登录态需求)
        config.setAllowCredentials(true);

        // 5. 暴露哪些头信息给前端（例如 Authorization）
        config.addExposedHeader("*");

        config.setMaxAge(3600L);

        // 针对所有路径生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}