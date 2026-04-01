package com.naxi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/common")
public class FileController {
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 获取项目根目录下的 images 目录
        System.out.println("当前图片file: " + file.getOriginalFilename());

        String destPath = System.getProperty("user.dir") + File.separator + "images";
        File destDir = new File(destPath);
        if (!destDir.exists()) destDir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destFile = new File(destDir, fileName);
        file.transferTo(destFile);

        // 返回前端可以直接访问的路径
        return ResponseEntity.ok(Map.of("url", "/images/" + fileName));
    }
}