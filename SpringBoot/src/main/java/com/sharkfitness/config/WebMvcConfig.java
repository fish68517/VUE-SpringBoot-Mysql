package com.sharkfitness.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration to register interceptors
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // Apply to all API endpoints
                .excludePathPatterns(
                        "/api/auth/register",    // Allow registration
                        "/api/auth/login"       // Allow login
                        /*"/api/search",           // Allow public search*/
                     /*   "/api/resources",        // Allow public resource browsing
                        "/api/resources/**"     // Allow public resource details*/
//                        "/api/dynamics",         // Allow public community feed
//                        "/api/dynamics/**",      // Allow public post details
                     /*   "/api/coaches",          // Allow public coach listing
                        "/api/coaches/**"        // Allow public coach profiles*/
                );
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOriginPatterns("*") // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根路径 (兼容 Windows/Linux)
        String projectPath = System.getProperty("user.dir");

        // 配置本地文件夹映射
        // 当访问 /uploads/** 时，映射到 file:项目根目录/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + projectPath + "/uploads/");
    }
}
