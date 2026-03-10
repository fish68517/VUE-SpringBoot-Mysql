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
            // 1. 获取项目根目录，并定位到 image 文件夹
            String projectPath = System.getProperty("user.dir");
            File imageDir = new File(projectPath, "image");

            // 如果 image 文件夹不存在，则自动创建
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            // 2. 获取原始文件名并生成新文件名 (建议用UUID防止同名文件被覆盖)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // 例如：生成 550e8400-e29b-41d4-a716-446655440000.jpg
            String newFileName = UUID.randomUUID().toString() + extension;

            // 如果你坚持想用原名(如 wuyuetian.jpg)，可以把上一行注释掉，换成这行：
            // String newFileName = originalFilename;

            // 3. 将文件保存到硬盘
            File dest = new File(imageDir, newFileName);
            file.transferTo(dest);

            // 4. 返回前端可访问的相对路径 URL
            String imageUrl = "/image/" + newFileName;

            return ApiResponse.success(imageUrl, "上传成功");

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}