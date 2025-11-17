package com.sharkfitness.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * File upload service interface
 * Handles image and video file uploads with validation
 */
public interface FileUploadService {

    /**
     * Upload an image file
     * @param file the image file to upload
     * @return the URL path to access the uploaded file
     */
    String uploadImage(MultipartFile file);

    /**
     * Upload a video file
     * @param file the video file to upload
     * @return the URL path to access the uploaded file
     */
    String uploadVideo(MultipartFile file);

    /**
     * Upload an avatar image
     * @param file the avatar image file to upload
     * @return the URL path to access the uploaded avatar
     */
    String uploadAvatar(MultipartFile file);
}
