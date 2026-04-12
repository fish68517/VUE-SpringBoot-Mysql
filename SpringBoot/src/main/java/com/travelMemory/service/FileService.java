package com.travelMemory.service;

import com.travelMemory.dto.MultimediaFileResponse;
import com.travelMemory.entity.MultimediaFile;
import com.travelMemory.entity.TravelRecord;
import com.travelMemory.repository.MultimediaFileRepository;
import com.travelMemory.repository.TravelRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final MultimediaFileRepository multimediaFileRepository;
    private final TravelRecordRepository travelRecordRepository;

    // 依然读取配置文件，默认为 uploads
    @Value("${file.upload.path:uploads}")
    private String configUploadPath;

    @Value("${file.upload.max-size:524288000}")
    private Long maxFileSize;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,mp4,avi,mov,mkv}")
    private String allowedTypes;

    /**
     * Upload a multimedia file chunk
     */
    public MultimediaFileResponse uploadFileChunk(
            Long travelRecordId,
            MultipartFile file,
            Integer chunkIndex,
            Integer totalChunks,
            String fileName,
            Long fileSize,
            Long userId) throws IOException {

        // 1. 验证权限
        TravelRecord record = travelRecordRepository.findByIdAndUserId(travelRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        if (fileSize > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit");
        }

        String fileExtension = getFileExtension(fileName);
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed");
        }

        // ======================= 路径修正核心代码开始 =======================

        // 2. 获取项目绝对根路径 (解决 Tomcat 临时目录问题)
        // 结果类似: D:\Acode\Android\complete\AA项目\SpringBoot
        String projectRoot = System.getProperty("user.dir");

        // 3. 构建基础上传目录 (项目根目录/uploads)
        // 注意：这里去掉 configUploadPath 可能带有的 "./" 前缀
        String cleanConfigPath = configUploadPath.replace("./", "").replace("/", File.separator);
        Path baseUploadDir = Paths.get(projectRoot, cleanConfigPath);

        // 4. 构建目标用户存储目录: uploads/records/{userId}
        Path targetUserDir = baseUploadDir.resolve("records").resolve(String.valueOf(userId));

        // 确保目录存在
        if (!Files.exists(targetUserDir)) {
            Files.createDirectories(targetUserDir);
        }

        // 5. 临时分片目录: uploads/temp/{uuid}
        // 分片不直接放在用户目录下，避免污染，合并完后删除
        Path tempSessionDir = baseUploadDir.resolve("temp").resolve(UUID.nameUUIDFromBytes((userId + fileName).getBytes()).toString());
        if (!Files.exists(tempSessionDir)) {
            Files.createDirectories(tempSessionDir);
        }

        // ======================= 路径修正核心代码结束 =======================

        // 6. 保存分片
        Path chunkPath = tempSessionDir.resolve("chunk_" + chunkIndex);
        file.transferTo(chunkPath.toFile());

        // 7. 检查是否所有分片已上传
        if (isAllChunksUploaded(tempSessionDir, totalChunks)) {

            // 8. 合并文件
            // 使用原始文件名 (匹配你的截图需求: sensoji-night.jpg)
            // 如果为了防止重名覆盖，可以在这里加个时间戳或UUID前缀，但为了匹配截图，这里用原名
            String finalFileName = fileName;
            Path finalFilePath = targetUserDir.resolve(finalFileName);

            // 如果文件已存在，为了避免报错，可以删除旧的或者重命名。这里选择覆盖。
            Files.deleteIfExists(finalFilePath);

            mergeChunks(tempSessionDir, finalFilePath, totalChunks);

            // 清理临时分片目录
            deleteDirectory(tempSessionDir.toFile());

            // 9. 保存到数据库
            // filePath 保存相对路径: records/2/sensoji-night.jpg
            // 这样前端访问时: http://localhost:8080/uploads/records/2/sensoji-night.jpg
            String relativeDbPath = "/uploads/records/" + userId + "/" + finalFileName;

            MultimediaFile multimediaFile = MultimediaFile.builder()
                    .travelRecordId(travelRecordId)
                    .fileName(fileName)
                    .filePath(relativeDbPath) // 存相对路径
                    .fileType(fileExtension)
                    .fileSize(fileSize)
                    .build();

            MultimediaFile savedFile = multimediaFileRepository.save(multimediaFile);

            // 返回给前端的可能是完整 URL，这取决于你的 DTO 转换逻辑
            // 这里返回实体即可
            return MultimediaFileResponse.from(savedFile);
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<MultimediaFileResponse> getFilesByTravelRecord(Long travelRecordId, Long userId) {
        travelRecordRepository.findByIdAndUserId(travelRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        List<MultimediaFile> files = multimediaFileRepository.findByTravelRecordId(travelRecordId);

        // 注意：如果你的 DTO 转换需要完整的 URL，可以在这里处理
        // 目前保持原样
        return files.stream()
                .map(MultimediaFileResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteFile(Long fileId, Long userId) throws IOException {
        MultimediaFile file = multimediaFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        travelRecordRepository.findByIdAndUserId(file.getTravelRecordId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Access denied"));

        // ======================= 删除逻辑修正 =======================
        String projectRoot = System.getProperty("user.dir");
        String cleanConfigPath = configUploadPath.replace("./", "").replace("/", File.separator);

        // 拼装绝对路径: D:/.../uploads/records/2/xxx.jpg
        Path fullFilePath = Paths.get(projectRoot, cleanConfigPath).resolve(file.getFilePath());

        if (Files.exists(fullFilePath)) {
            Files.delete(fullFilePath);
        }
        // ==========================================================

        multimediaFileRepository.delete(file);
    }

    // 辅助方法保持不变
    private boolean isAllowedFileType(String fileExtension) {
        String[] types = allowedTypes.split(",");
        for (String type : types) {
            if (type.trim().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    private boolean isAllChunksUploaded(Path sessionDir, Integer totalChunks) {
        try {
            long chunkCount = Files.list(sessionDir)
                    .filter(p -> p.getFileName().toString().startsWith("chunk_"))
                    .count();
            return chunkCount == totalChunks;
        } catch (IOException e) {
            return false;
        }
    }

    private void mergeChunks(Path sessionDir, Path finalFilePath, Integer totalChunks) throws IOException {
        try (var output = Files.newOutputStream(finalFilePath)) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkPath = sessionDir.resolve("chunk_" + i);
                byte[] chunkData = Files.readAllBytes(chunkPath);
                output.write(chunkData);
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    public void deleteFilesByTravelRecord(Long travelRecordId) throws IOException {
        List<MultimediaFile> files = multimediaFileRepository.findByTravelRecordId(travelRecordId);
        String projectRoot = System.getProperty("user.dir");
        String cleanConfigPath = configUploadPath.replace("./", "").replace("/", File.separator);
        Path baseDir = Paths.get(projectRoot, cleanConfigPath);

        for (MultimediaFile file : files) {
            Path filePath = baseDir.resolve(file.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }
        multimediaFileRepository.deleteByTravelRecordId(travelRecordId);
    }
}