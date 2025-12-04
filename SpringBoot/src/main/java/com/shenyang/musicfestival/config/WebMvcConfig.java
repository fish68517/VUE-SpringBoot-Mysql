package com.shenyang.musicfestival.config;

import com.shenyang.musicfestival.interceptor.JwtAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for registering interceptors
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                // Public endpoints that don't require authentication
                "/auth/**",
                "/festival/info",
                "/festival/schedule",
                "/artists",
                "/artists/**",
                "/weather",
                "/transport",
                "/articles",
                "/articles/**",
                "/products",
                "/products/**",
                "/products/categories",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**"
            );
    }

}
