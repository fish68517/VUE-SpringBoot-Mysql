package com.travelMemory.dto;

import com.travelMemory.validation.ValidFileSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {

    @NotNull(message = "Travel record ID is required")
    private Long travelRecordId;

    @NotBlank(message = "Chunk hash is required")
    private String chunkHash;

    @NotNull(message = "Chunk index is required")
    @Min(value = 0, message = "Chunk index must be non-negative")
    private Integer chunkIndex;

    @NotNull(message = "Total chunks is required")
    @Min(value = 1, message = "Total chunks must be at least 1")
    private Integer totalChunks;

    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be greater than 0")
    @ValidFileSize(maxSizeInMB = 500, message = "File size must not exceed 500 MB")
    private Long fileSize;

    @NotBlank(message = "File name is required")
    private String fileName;
}
