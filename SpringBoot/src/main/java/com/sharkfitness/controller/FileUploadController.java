package com.sharkfitness.controller;

import com.sharkfitness.service.FileUploadService;
import com.sharkfitness.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * File upload controller
 * Handles image and video file uploads
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload an image file
     * @param file the image file to upload
     * @return API response with file URL
     */
    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("Received image upload request: {}", file.getOriginalFilename());
        
        String fileUrl = fileUploadService.uploadImage(file);
        
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("filename", file.getOriginalFilename());
        
        return ApiResponse.success(result);
    }

    /**
     * Upload a video file
     * @param file the video file to upload
     * @return API response with file URL
     */
    @PostMapping("/video")
    public ApiResponse<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        log.info("Received video upload request: {}", file.getOriginalFilename());
        
        String fileUrl = fileUploadService.uploadVideo(file);
        
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("filename", file.getOriginalFilename());
        
        return ApiResponse.success(result);
    }
}
