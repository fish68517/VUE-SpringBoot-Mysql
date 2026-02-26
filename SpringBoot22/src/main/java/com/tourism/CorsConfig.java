package com.tourism;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加映射路径，"/**" 表示拦截所有请求
        registry.addMapping("/**")
                // 允许的跨域来源，"*" 代表所有来源。
                // 注意：在 Spring Boot 2.4.0 及以上版本，如果 allowCredentials 为 true，请使用 allowedOriginPatterns("*") 而不是 allowedOrigins("*")
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                // 允许的请求头
                .allowedHeaders("*")
                // 是否允许携带凭证（如 Cookie、Token 等），前端使用了请求头传递用户信息，这里必须为 true
//                /*.allowCredentials(true)*/
                // 预检请求（OPTIONS）的缓存时间，单位是秒
                .maxAge(3600);
    }


    // 【新增下面这个方法】用于映射本地图片目录为静态资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String projectPath = System.getProperty("user.dir");
        // 当访问 http://localhost:8080/api/images/xxx.jpg 时，去本地项目根目录的 images 找
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + projectPath + "/images/");
    }
}