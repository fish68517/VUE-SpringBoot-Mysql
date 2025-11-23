package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    // 上传路径：指向项目的 src/main/resources/static/images/ 目录
    // System.getProperty("user.dir") 获取项目根目录
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return (ApiResponse<String>) ApiResponse.error("上传文件不能为空");
        }

        // 1. 确保目录存在
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 2. 生成唯一文件名 (UUID + 原始后缀)
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 3. 保存文件
        try {
            File dest = new File(directory, newFilename);
            file.transferTo(dest);

            // 4. 返回可访问的 URL (相对路径，配合 CorsConfig 映射)
            // 假设映射路径为 /images/**
            String fileUrl = "http://localhost:8080/images/" + newFilename;
            return ApiResponse.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return (ApiResponse<String>) ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}