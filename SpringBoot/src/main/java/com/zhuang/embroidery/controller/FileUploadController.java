package com.zhuang.embroidery.controller;

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

    @PostMapping("/image")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        try {
            // 1. 获取项目根目录下的 images 文件夹路径
            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + File.separator + "images";

            // 2. 如果 images 文件夹不存在，则创建它
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 3. 生成唯一文件名，防止覆盖 (例如: uuid.jpg)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;

            // 4. 保存文件到本地物理硬盘
            File dest = new File(uploadDir + File.separator + newFileName);
            file.transferTo(dest);

            // 5. 返回前端可访问的相对 URL 路径
            // 返回结果类似于："/images/123e4567-e89b-12d3-a456-426614174000.jpg"
            return "/images/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败：" + e.getMessage();
        }
    }
}