package com.powerinspection.service;

import com.powerinspection.common.BusinessException;
import com.powerinspection.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AppProperties appProperties;

    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择图片文件");
        }
        try {
            Path dir = Paths.get(appProperties.getImageDir()).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String originalName = file.getOriginalFilename();
            String extension = StringUtils.getFilenameExtension(originalName);
            String fileName = UUID.randomUUID().toString().replace("-", "");
            if (extension != null && !extension.isBlank()) {
                fileName = fileName + "." + extension.toLowerCase();
            }
            Files.copy(file.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new BusinessException("图片保存失败");
        }
    }
}
