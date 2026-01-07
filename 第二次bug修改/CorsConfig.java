package com.xingluo.petshop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局配置：跨域 + 静态资源映射
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置静态资源映射
     * 将 URL 中的 /images/** 映射到本地磁盘目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取本地存图片的绝对路径
        String imageDir = "file:" + System.getProperty("user.dir") + "/src/main/resources/static/images/";

        // 映射: 访问 http://localhost:8080/images/xxx.jpg -> 读取本地目录
        registry.addResourceHandler("/images/**")
                .addResourceLocations(imageDir);


        // 获取项目根目录 (例如 D:\Projects\petshop)
        String path = System.getProperty("user.dir");

        // 统一处理路径分隔符，防止 Windows/Mac 差异
        String uploadDir = "file:" + path + "/uploads/";

        // 映射:
        // 访问 http://localhost:8080/uploads/products/p1.jpg

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);

        // 打印一下路径方便调试 (可选)
        System.out.println("图片映射路径: " + uploadDir);
    }
}