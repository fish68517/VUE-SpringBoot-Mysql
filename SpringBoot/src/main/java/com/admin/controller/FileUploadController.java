package com.admin.controller;

import com.admin.vo.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @PostMapping
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件不能为空");
        }

        try {
            // 1. 指定相对于项目根目录的文件夹
            File dir = new File("image");

            // 如果目录不存在则创建
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 生成唯一文件名
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 3. 【关键修改】使用 dir.getAbsolutePath() 获取绝对路径！
            // 这样 Tomcat 就不会把路径拼接到临时目录里去了
            File dest = new File(dir.getAbsolutePath(), filename);

            // 4. 保存文件
            file.transferTo(dest);

            // 5. 返回前端可访问的相对路径
            return ApiResponse.success("上传成功", "/image/" + filename);

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
}