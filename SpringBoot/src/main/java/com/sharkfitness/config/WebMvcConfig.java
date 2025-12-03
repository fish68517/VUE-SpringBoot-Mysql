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
                        "/api/auth/login",       // Allow login
                        "/api/search",           // Allow public search
                        "/api/resources",        // Allow public resource browsing
                        "/api/resources/**",     // Allow public resource details
//                        "/api/dynamics",         // Allow public community feed
//                        "/api/dynamics/**",      // Allow public post details
                        "/api/coaches",          // Allow public coach listing
                        "/api/coaches/**"        // Allow public coach profiles
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
        // 映射 URL 路径 /videos/** 到 classpath 下的 videos 目录
 /*       这行代码告诉 Spring Boot：“如果用户访问 /videos*//**，请去 src/main/resources/videos/ 找文件”。
         但是，你的文件并不在那里，你的文件在 src/main/resources/static/videos/。*/
        /*registry.addResourceHandler("/videos/**")
                .addResourceLocations("classpath:/videos/");


        // 2. 映射图片目录 (这里是新增的代码)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/images/");*/

        // 如果视频存在本地磁盘（例如 D盘），这是生产环境更推荐的做法，避免jar包过大
        // registry.addResourceHandler("/videos/**")
        //         .addResourceLocations("file:D:/data/videos/");
    }
}
