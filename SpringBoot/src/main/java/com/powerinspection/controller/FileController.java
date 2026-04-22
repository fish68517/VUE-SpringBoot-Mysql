package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.config.AppProperties;
import com.powerinspection.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final AppProperties appProperties;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, String>> uploadImage(@RequestPart("file") MultipartFile file) {
        String fileName = fileStorageService.saveImage(file);
        return ApiResponse.ok(Map.of(
                "imageName", fileName,
                "imageUrl", appProperties.getBaseUrl() + "/images/" + fileName
        ));
    }
}
