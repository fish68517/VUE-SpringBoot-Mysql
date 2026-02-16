package com.zhuang.embroidery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录的绝对路径
        String projectPath = System.getProperty("user.dir");

        System.out.println("项目根目录绝对路径: " + projectPath);
        // 映射规则：当请求路径以 /images/ 开头时
        registry.addResourceHandler("/images/**")
                // file: 前缀告诉 Spring Boot 这是一个文件系统绝对路径，而非 classpath 路径
                .addResourceLocations("file:" + projectPath + File.separator + "images" + File.separator);
    }
}