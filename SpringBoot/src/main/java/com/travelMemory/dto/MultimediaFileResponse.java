package com.travelMemory.dto;

import com.travelMemory.entity.MultimediaFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultimediaFileResponse {

    private Long id;
    private Long travelRecordId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;

    /**
     * Convert MultimediaFile entity to response DTO
     * @param file the multimedia file entity
     * @return MultimediaFileResponse
     */
    public static MultimediaFileResponse from(MultimediaFile file) {
        return MultimediaFileResponse.builder()
                .id(file.getId())
                .travelRecordId(file.getTravelRecordId())
                .fileName(file.getFileName())
                .filePath(file.getFilePath())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .uploadDate(file.getUploadDate())
                .build();
    }
}
