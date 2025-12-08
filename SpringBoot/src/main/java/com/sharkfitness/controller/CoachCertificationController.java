package com.sharkfitness.controller;

import com.sharkfitness.CoachCertificationService;
import com.sharkfitness.entity.CoachCertification;
import com.sharkfitness.entity.User;
import com.sharkfitness.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coach/certifications")
public class CoachCertificationController {

    @Autowired
    private CoachCertificationService certService;

    // 获取项目根路径下的 uploads 目录
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    /**
     * 上传证书图片
     */
    /**
     * 上传证书图片
     */
    @PostMapping("/upload")
    public ApiResponse<String> uploadCertImage(@RequestParam("file") MultipartFile file, @RequestAttribute("currentUser") User currentUser) {
        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }

        System.out.println("上传文件: " + file.getOriginalFilename());
        System.out.println("上传文件大小: " + file.getSize());
        System.out.println("上传文件类型: " + file.getContentType());
        System.out.println("当前用户: " + currentUser.getId());

        try {
            // 1. 根据用户ID构建专属的上传目录
            String userSpecificUploadPath = UPLOAD_DIR + currentUser.getId() + "/";
            System.out.println("用户专属上传目录: " + userSpecificUploadPath);
            File userUploadDir = new File(userSpecificUploadPath);
            if (!userUploadDir.exists()) {
                // 创建包括所有父目录在内的文件夹
                userUploadDir.mkdirs();
            }

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            // 确保文件名和扩展名是有效的
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 3. 将文件保存到用户专属目录中
            File dest = new File(userSpecificUploadPath + newFilename);
            file.transferTo(dest);

            // 4. 返回与保存路径完全匹配的可访问URL
            String fileUrl = "/uploads/" + currentUser.getId() + "/" + newFilename;

            return ApiResponse.success(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }


    @GetMapping
    public ApiResponse<List<CoachCertification>> getMyCerts(@RequestAttribute("currentUser") User currentUser) {
        Long userId = currentUser.getId(); // 获取当前登录ID
        return ApiResponse.success(certService.getMyCertifications(userId));
    }

    @PostMapping
    public ApiResponse<?> addCert(@RequestBody CoachCertification cert,@RequestAttribute("currentUser") User currentUser) {
        Long userId = currentUser.getId();
        cert.setCoachId(userId);
        certService.addCertification(cert);
        return ApiResponse.success();
    }

    @PutMapping
    public ApiResponse<?> updateCert(@RequestBody CoachCertification cert,@RequestAttribute("currentUser") User currentUser) {
        Long userId = currentUser.getId();
        cert.setCoachId(userId);
        certService.updateCertification(cert);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCert(@PathVariable Long id,@RequestAttribute("currentUser") User currentUser) {
        Long userId = currentUser.getId();
        certService.deleteCertification(id, userId);
        return ApiResponse.success();
    }
}