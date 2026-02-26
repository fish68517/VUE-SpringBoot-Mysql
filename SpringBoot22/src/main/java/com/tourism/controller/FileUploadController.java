package com.tourism.controller;

import com.tourism.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error("上传文件不能为空");
        }
        try {
            // 获取项目根目录下的 images 文件夹
            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + File.separator + "images";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 如果目录不存在则创建
            }

            // 获取原始文件名和后缀
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成唯一的 UUID 文件名，防止重名覆盖
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件到本地磁盘
            File dest = new File(uploadDir + File.separator + newFilename);
            file.transferTo(dest);

            // 返回图片的相对访问路径 (例如: /images/xxx.jpg)
            String imageUrl = "/images/" + newFilename;
            return ApiResponse.success(imageUrl);
        } catch (IOException e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}