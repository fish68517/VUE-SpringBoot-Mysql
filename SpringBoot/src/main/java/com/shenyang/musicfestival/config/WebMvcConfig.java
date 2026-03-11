package com.shenyang.musicfestival.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 极简版 Web MVC 配置，仅处理跨域
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 仅保留跨域支持 (CORS) 即可，前端就能正常请求所有接口了
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                   // 所有接口
                .allowedOriginPatterns("*")          // 支持所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 支持所有常用方法
                .allowedHeaders("*")                 // 支持所有头
                .allowCredentials(true)              // 允许携带 Cookie/凭证
                .maxAge(3600);                       // 预检请求缓存时间
    }

    // 已完全移除 addInterceptors 方法，不再进行任何 JWT 拦截

    // 新增：配置本地图片文件夹映射，让前端可以直接通过 URL 访问
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录
        String projectPath = System.getProperty("user.dir");
        // 拼接出本地 image 文件夹的绝对路径 (注意末尾要有斜杠)
        String imagePath = "file:" + projectPath + File.separator + "images" + File.separator;

        // 告诉 Spring Boot：当访问 /image/** 时，去本地的 image 文件夹下找资源
        registry.addResourceHandler("/images/**")
                .addResourceLocations(imagePath);

    }

/*    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前项目的根目录路径
        String projectPath = System.getProperty("user.dir");

        // 将 HTTP 请求的 /images/** 映射到本地硬盘的物理文件夹 images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + projectPath + "/images/");
    }*/
}