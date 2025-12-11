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
public class FileServiceCopy {

    private final MultimediaFileRepository multimediaFileRepository;
    private final TravelRecordRepository travelRecordRepository;

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${file.upload.max-size:524288000}")
    private Long maxFileSize;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,mp4,avi,mov,mkv}")
    private String allowedTypes;

    /**
     * Upload a multimedia file chunk
     * @param travelRecordId the travel record ID
     * @param file the file chunk
     * @param chunkIndex the chunk index
     * @param totalChunks the total number of chunks
     * @param fileName the original file name
     * @param fileSize the total file size
     * @param userId the user ID (for ownership verification)
     * @return MultimediaFileResponse if all chunks are uploaded, null otherwise
     * @throws IllegalArgumentException if validation fails
     * @throws IOException if file operation fails
     */
    public MultimediaFileResponse uploadFileChunk(
            Long travelRecordId,
            MultipartFile file,
            Integer chunkIndex,
            Integer totalChunks,
            String fileName,
            Long fileSize,
            Long userId) throws IOException {

        // Verify travel record exists and user is the owner
        TravelRecord record = travelRecordRepository.findByIdAndUserId(travelRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        // Validate file size
        if (fileSize > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of " + maxFileSize + " bytes");
        }

        // Validate file type
        String fileExtension = getFileExtension(fileName);
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + allowedTypes);
        }

        // Create upload directory if it doesn't exist
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Create a temporary directory for chunks
        String uploadSessionId = UUID.randomUUID().toString();
        Path sessionDir = uploadDir.resolve(uploadSessionId);
        if (!Files.exists(sessionDir)) {
            Files.createDirectories(sessionDir);
        }

        // Save chunk
        Path chunkPath = sessionDir.resolve("chunk_" + chunkIndex);
        file.transferTo(chunkPath.toFile());

        // Check if all chunks are uploaded
        if (isAllChunksUploaded(sessionDir, totalChunks)) {
            // Merge chunks into final file
            String finalFileName = UUID.randomUUID() + "." + fileExtension;
            Path finalFilePath = uploadDir.resolve(finalFileName);
            mergeChunks(sessionDir, finalFilePath, totalChunks);

            // Clean up session directory
            deleteDirectory(sessionDir.toFile());

            // Save file metadata to database
            MultimediaFile multimediaFile = MultimediaFile.builder()
                    .travelRecordId(travelRecordId)
                    .fileName(fileName)
                    .filePath(finalFileName)
                    .fileType(fileExtension)
                    .fileSize(fileSize)
                    .build();

            MultimediaFile savedFile = multimediaFileRepository.save(multimediaFile);
            return MultimediaFileResponse.from(savedFile);
        }

        return null; // Chunk uploaded, waiting for more chunks
    }

    /**
     * Get all multimedia files for a travel record
     * @param travelRecordId the travel record ID
     * @param userId the user ID (for ownership verification)
     * @return List of MultimediaFileResponse
     * @throws IllegalArgumentException if travel record not found or access denied
     */
    @Transactional(readOnly = true)
    public List<MultimediaFileResponse> getFilesByTravelRecord(Long travelRecordId, Long userId) {
        // Verify travel record exists and user is the owner
        travelRecordRepository.findByIdAndUserId(travelRecordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

        List<MultimediaFile> files = multimediaFileRepository.findByTravelRecordId(travelRecordId);
        return files.stream()
                .map(MultimediaFileResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Delete a multimedia file
     * @param fileId the file ID
     * @param userId the user ID (for ownership verification)
     * @throws IllegalArgumentException if file not found or access denied
     * @throws IOException if file deletion fails
     */
    public void deleteFile(Long fileId, Long userId) throws IOException {
        // Get file
        MultimediaFile file = multimediaFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Verify ownership by checking if user owns the travel record
        travelRecordRepository.findByIdAndUserId(file.getTravelRecordId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Access denied"));

        // Delete file from filesystem
        Path filePath = Paths.get(uploadPath).resolve(file.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete from database
        multimediaFileRepository.delete(file);
    }

    /**
     * Delete all multimedia files for a travel record
     * @param travelRecordId the travel record ID
     * @throws IOException if file deletion fails
     */
    public void deleteFilesByTravelRecord(Long travelRecordId) throws IOException {
        List<MultimediaFile> files = multimediaFileRepository.findByTravelRecordId(travelRecordId);

        for (MultimediaFile file : files) {
            // Delete file from filesystem
            Path filePath = Paths.get(uploadPath).resolve(file.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }

        // Delete all from database
        multimediaFileRepository.deleteByTravelRecordId(travelRecordId);
    }

    /**
     * Check if file type is allowed
     * @param fileExtension the file extension
     * @return true if allowed, false otherwise
     */
    private boolean isAllowedFileType(String fileExtension) {
        String[] types = allowedTypes.split(",");
        for (String type : types) {
            if (type.trim().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get file extension from file name
     * @param fileName the file name
     * @return the file extension
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Check if all chunks are uploaded
     * @param sessionDir the session directory
     * @param totalChunks the total number of chunks
     * @return true if all chunks are uploaded, false otherwise
     */
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

    /**
     * Merge chunks into final file
     * @param sessionDir the session directory
     * @param finalFilePath the final file path
     * @param totalChunks the total number of chunks
     * @throws IOException if merge fails
     */
    private void mergeChunks(Path sessionDir, Path finalFilePath, Integer totalChunks) throws IOException {
        try (var output = Files.newOutputStream(finalFilePath)) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkPath = sessionDir.resolve("chunk_" + i);
                byte[] chunkData = Files.readAllBytes(chunkPath);
                output.write(chunkData);
            }
        }
    }

    /**
     * Delete directory recursively
     * @param directory the directory to delete
     */
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
}
