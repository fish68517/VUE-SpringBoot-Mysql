package com.sharkfitness.service.impl;

import com.sharkfitness.config.FileUploadConfig;
import com.sharkfitness.exception.ValidationException;
import com.sharkfitness.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * Save file to disk and return URL path
     */
    private String saveFile(MultipartFile file, String subDirectory) {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String uniqueFilename = generateUniqueFilename(extension);
            
            // Create file path
            String directoryPath = fileUploadConfig.getUploadPath() + File.separator + subDirectory;
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Save file
            String filePath = directoryPath + File.separator + uniqueFilename;
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);
            
            log.info("File saved successfully: {}", filePath);
            
            // Return URL path (relative path for frontend access)
            return "/uploads/" + subDirectory + "/" + uniqueFilename;
            
        } catch (IOException e) {
            log.error("Failed to save file: {}", e.getMessage(), e);
            throw new ValidationException("Failed to save file: " + e.getMessage());
        }
    }

    /**
     * Generate unique filename using timestamp and UUID
     */
    private String generateUniqueFilename(String extension) {
        return System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;
    }
}
