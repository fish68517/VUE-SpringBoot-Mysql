package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.MultimediaFileResponse;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class FileController {

    private final FileService fileService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Upload a multimedia file chunk
     * @param travelRecordId the travel record ID
     * @param file the file chunk
     * @param chunkIndex the chunk index
     * @param totalChunks the total number of chunks
     * @param fileName the original file name
     * @param fileSize the total file size
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with MultimediaFileResponse if upload is complete
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MultimediaFileResponse>> uploadFile(
            @RequestParam Long travelRecordId,
            @RequestParam MultipartFile file,
            @RequestParam Integer chunkIndex,
            @RequestParam Integer totalChunks,
            @RequestParam String fileName,
            @RequestParam Long fileSize,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Upload file chunk
            MultimediaFileResponse response = fileService.uploadFileChunk(
                    travelRecordId, file, chunkIndex, totalChunks, fileName, fileSize, userId);

            if (response != null) {
                // All chunks uploaded, file is complete
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.created(response));
            } else {
                // Chunk uploaded, waiting for more chunks
                return ResponseEntity.ok(ApiResponse.success("Chunk uploaded successfully", null));
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Get all multimedia files for a travel record
     * @param travelRecordId the travel record ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with List of MultimediaFileResponse
     */
    @GetMapping("/travel/{travelRecordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MultimediaFileResponse>>> getFilesByTravelRecord(
            @PathVariable Long travelRecordId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Get files
            List<MultimediaFileResponse> files = fileService.getFilesByTravelRecord(travelRecordId, userId);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve files: " + e.getMessage()));
        }
    }

    /**
     * Delete a multimedia file
     * @param fileId the file ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{fileId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable Long fileId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Delete file
            fileService.deleteFile(fileId, userId);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "File deletion failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Extract user ID from JWT token in Authorization header
     * @param authHeader the Authorization header
     * @return the user ID or null if token is invalid
     */
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        try {
            String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
            return userIdStr != null ? Long.parseLong(userIdStr) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
