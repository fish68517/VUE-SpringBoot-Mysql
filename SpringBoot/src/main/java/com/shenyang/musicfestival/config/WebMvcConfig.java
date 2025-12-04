package com.shenyang.musicfestival.config;

import com.shenyang.musicfestival.interceptor.JwtAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for registering interceptors
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;


    // 解决办法 1：配置跨域支持 (CORS)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                   // 所有接口
                .allowedOriginPatterns("*")          // 支持所有来源 (建议开发环境用，生产环境换成具体域名)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 支持的方法
                .allowedHeaders("*")                 // 支持所有头
                .allowCredentials(true)              // 允许携带 Cookie/凭证
                .maxAge(3600);                       // 预检请求缓存时间
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(

                    // 解决办法 2：确保你的登录/注册接口被排除
                    // 如果你的 Controller 是 @RequestMapping("/api/users")，这里必须放行
                    "/api/users/login",
                    "/api/users/register",
                    "/api/users/**",      // 或者直接放行 /api/users 下的所有接口
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
