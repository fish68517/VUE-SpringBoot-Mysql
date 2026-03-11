package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.util.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用文件上传 Controller
 */
@RestController
@RequestMapping("/api/upload")
public class FileController {

    /**
     * 上传图片接口
     * POST /api/upload/image
     */
    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }

        try {
            // 1. 获取项目根目录，并定位到 images/products 文件夹
            String projectPath = System.getProperty("user.dir");
            File imageDir = new File(projectPath, "images/products");

            // 如果文件夹不存在，则自动创建
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            // 2. 获取原始文件名并生成新文件名 (用UUID防止同名文件被覆盖)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + extension;

            // 3. 将文件保存到硬盘
            File dest = new File(imageDir, newFileName);
            file.transferTo(dest);

            // 4. 返回前端可访问的相对路径 URL
            String imageUrl = "/images/products/" + newFileName;
            return ApiResponse.success(imageUrl, "上传成功");

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("上传图片失败: " + e.getMessage());
        }
    }
}