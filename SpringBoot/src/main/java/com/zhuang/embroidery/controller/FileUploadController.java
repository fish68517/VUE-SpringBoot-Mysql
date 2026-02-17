package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FileUploadController {

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.badRequest("上传失败，请选择文件");
        }

        try {
            // 1. 获取项目根目录下的 images/upload 文件夹路径
            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + File.separator + "images" + File.separator + "upload";

            // 2. 如果文件夹不存在，则自动创建
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 3. 生成唯一文件名，防止覆盖
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String newFileName = UUID.randomUUID().toString() + extension;

            // 4. 保存文件到本地物理硬盘
            File dest = new File(uploadDir + File.separator + newFileName);
            file.transferTo(dest);

            // 5. 返回前端可访问的相对 URL 路径
            // 前提是你之前的 WebConfig 已经配置了 registry.addResourceHandler("/images/**")
            String fileUrl = "/images/upload/" + newFileName;
            log.info("图片上传成功，保存路径: {}", fileUrl);

            return ApiResponse.success(fileUrl);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return ApiResponse.serverError("文件上传失败：" + e.getMessage());
        }
    }
}