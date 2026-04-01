package com.naxi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 获取项目根目录下的 images 文件夹的绝对路径
        String imagesPath = System.getProperty("user.dir") + File.separator + "images" + File.separator;

        System.out.println("映射 /images/** 到物理路径: " + imagesPath);
        // 2. 将 URL 中以 /images/ 开头的请求映射到本地物理路径
        // 注意：file: 前缀是必须的，代表物理磁盘路径
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagesPath);
    }
}