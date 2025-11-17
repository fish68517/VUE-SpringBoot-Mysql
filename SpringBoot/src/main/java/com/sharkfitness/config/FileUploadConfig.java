package com.sharkfitness.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.MultipartConfigElement;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.File;

/**
 * File upload configuration
 * Configures max file sizes and upload directory structure
 */
@Getter
@Configuration
public class FileUploadConfig {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.max-size.image:5242880}")  // 5MB default
    private long maxImageSize;

    @Value("${file.max-size.video:104857600}")  // 100MB default
    private long maxVideoSize;

    /**
     * Configure multipart file upload settings
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set max file size to the larger of the two (video size)
        factory.setMaxFileSize(DataSize.ofBytes(maxVideoSize));
        factory.setMaxRequestSize(DataSize.ofBytes(maxVideoSize + 1048576)); // Add 1MB for other form data
        
        return factory.createMultipartConfig();
    }

    /**
     * Create upload directory structure on startup
     */
    @PostConstruct
    public void init() {
        createDirectoryIfNotExists(uploadPath);
        createDirectoryIfNotExists(uploadPath + "/images");
        createDirectoryIfNotExists(uploadPath + "/videos");
        createDirectoryIfNotExists(uploadPath + "/avatars");
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Created upload directory: " + path);
            }
        }
    }

    /**
     * Get the absolute path for uploads
     */
    public String getAbsoluteUploadPath() {
        File file = new File(uploadPath);
        return file.getAbsolutePath();
    }
}
