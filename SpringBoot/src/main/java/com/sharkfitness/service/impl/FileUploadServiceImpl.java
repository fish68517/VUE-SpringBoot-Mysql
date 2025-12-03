package com.sharkfitness.service.impl;

import com.sharkfitness.config.FileUploadConfig;
import com.sharkfitness.exception.ValidationException;
import com.sharkfitness.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * File upload service implementation
 * Handles file validation, storage, and URL generation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadConfig fileUploadConfig;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mpeg"
    );

    @Override
    public String uploadImage(MultipartFile file) {
        log.debug("Uploading image file: {}", file.getOriginalFilename());
        validateImageFile(file);
        return saveFile(file, "images");
    }

    @Override
    public String uploadVideo(MultipartFile file) {
        log.debug("Uploading video file: {}", file.getOriginalFilename());
        validateVideoFile(file);
        return saveFile(file, "static/videos");
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        log.debug("Uploading avatar file: {}", file.getOriginalFilename());
        validateImageFile(file);
        return saveFile(file, "avatars");
    }

    /**
     * Validate image file type and size
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > fileUploadConfig.getMaxImageSize()) {
            throw new ValidationException("Image size exceeds 5MB limit");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ValidationException("Invalid image format. Allowed formats: JPG, PNG, GIF");
        }
    }

    /**
     * Validate video file type and size
     */
    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > fileUploadConfig.getMaxVideoSize()) {
            throw new ValidationException("Video size exceeds 100MB limit");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
            throw new ValidationException("Invalid video format. Allowed formats: MP4, AVI, MPEG");
        }
    }



    /**
     * Save file to src/main/resources/static/{subDirectory}
     */
    private String saveFile(MultipartFile file, String subDirectory) {
        // subDirectory 应该是 "images" 或 "videos"
        System.out.println("Processing file upload for type: " + subDirectory);

        try {
            // 1. 获取文件名和后缀
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 2. 生成唯一文件名
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // 3. 【关键修改】获取项目根目录的绝对路径 (即你的项目文件夹路径)
            String projectRoot = System.getProperty("user.dir");

            // 4. 拼凑出 src/main/resources/static/images 的绝对路径
            // 路径格式： D:\MyProjects\shark-fitness\src\main\resources\static\images
            String saveDirectoryPath = projectRoot + File.separator + "src" +
                    File.separator + "main" +
                    File.separator + "resources" +
                    File.separator + "static" +
                    File.separator + subDirectory;

            System.out.println("Save directory path: " + saveDirectoryPath);
            File directory = new File(saveDirectoryPath);

            // 5. 如果目录不存在，强制创建 (解决了 FileNotFoundException)
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("Created directory: " + saveDirectoryPath);
                } else {
                    // 如果创建失败，可能是权限问题，或者路径太长
                    System.err.println("Failed to create directory: " + saveDirectoryPath);
                }
            }

            // 6. 保存文件到该路径
            String filePath = saveDirectoryPath + File.separator + uniqueFilename;
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);

            log.info("File saved to source directory: {}", filePath);

            // 7. 返回访问路径
            // 因为放在了 static 下，Spring Boot 默认会自动映射
            // 如果是 static/images/123.png -> 访问地址是 /images/123.png
            return "/" + subDirectory + "/" + uniqueFilename;

        } catch (IOException e) {
            log.error("Failed to save file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    /**
     * Generate unique filename using timestamp and UUID
     */
    private String generateUniqueFilename(String extension) {
        return System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;
    }
}
