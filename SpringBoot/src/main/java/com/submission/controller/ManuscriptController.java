package com.submission.controller;

import com.submission.dto.ManuscriptDTO;
import com.submission.entity.Manuscript;
import com.submission.service.ManuscriptService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Manuscript Controller - Handles manuscript submission and management
 */
@RestController
@RequestMapping("/api/manuscripts")
@RequiredArgsConstructor
public class ManuscriptController {

    private final ManuscriptService manuscriptService;

    /**
     * Submit a new manuscript
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ManuscriptDTO>> submitManuscript(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("title") String title,
            @RequestParam("abstractText") String abstractText,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session) {
        try {
            Long authorId = (Long) session.getAttribute("userId");
            if (authorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            ManuscriptDTO manuscriptDTO = ManuscriptDTO.builder()
                    .authorId(authorId)
                    .categoryId(categoryId)
                    .title(title)
                    .abstractText(abstractText)
                    .content(content)
                    .build();

            Manuscript manuscript = manuscriptService.submitManuscript(manuscriptDTO, file);
            ManuscriptDTO responseDTO = manuscriptService.convertToDTO(manuscript);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Manuscript submitted successfully", responseDTO));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("File upload failed: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get manuscript by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ManuscriptDTO>> getManuscript(@PathVariable Long id) {
        try {
            Manuscript manuscript = manuscriptService.getManuscriptById(id);
            if (manuscript == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Manuscript not found"));
            }

            ManuscriptDTO responseDTO = manuscriptService.convertToDTO(manuscript);
            return ResponseEntity.ok(ApiResponse.success("Manuscript retrieved", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get manuscript detail with review history
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<?>> getManuscriptDetail(@PathVariable Long id) {
        try {
            var manuscriptDetail = manuscriptService.getManuscriptDetail(id);
            return ResponseEntity.ok(ApiResponse.success("Manuscript detail retrieved", manuscriptDetail));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get review history for a manuscript
     */
    @GetMapping("/{id}/review-history")
    public ResponseEntity<ApiResponse<?>> getReviewHistory(@PathVariable Long id) {
        try {
            var reviewHistory = manuscriptService.getReviewHistory(id);
            return ResponseEntity.ok(ApiResponse.success("Review history retrieved", reviewHistory));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all manuscripts by author
     */
    @GetMapping("/author/list")
    public ResponseEntity<ApiResponse<List<ManuscriptDTO>>> getAuthorManuscripts(HttpSession session) {
        try {
            Long authorId = (Long) session.getAttribute("userId");
            if (authorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<Manuscript> manuscripts = manuscriptService.getManuscriptsByAuthorId(authorId);
            List<ManuscriptDTO> responseDTOs = manuscriptService.convertToDTOList(manuscripts);

            return ResponseEntity.ok(ApiResponse.success("Manuscripts retrieved", responseDTOs));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all manuscripts
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ManuscriptDTO>>> getAllManuscripts() {
        try {
            List<Manuscript> manuscripts = manuscriptService.getAllManuscripts();
            List<ManuscriptDTO> responseDTOs = manuscriptService.convertToDTOList(manuscripts);

            return ResponseEntity.ok(ApiResponse.success("Manuscripts retrieved", responseDTOs));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update manuscript
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ManuscriptDTO>> updateManuscript(
            @PathVariable Long id,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "abstractText", required = false) String abstractText,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session) {
        try {
            Long authorId = (Long) session.getAttribute("userId");
            if (authorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            Manuscript existingManuscript = manuscriptService.getManuscriptById(id);
            if (existingManuscript == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Manuscript not found"));
            }

            if (!existingManuscript.getAuthorId().equals(authorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You do not have permission to update this manuscript"));
            }

            ManuscriptDTO manuscriptDTO = ManuscriptDTO.builder()
                    .id(id)
                    .authorId(authorId)
                    .categoryId(categoryId != null ? categoryId : existingManuscript.getCategoryId())
                    .title(title != null ? title : existingManuscript.getTitle())
                    .abstractText(abstractText != null ? abstractText : existingManuscript.getAbstractText())
                    .content(content != null ? content : existingManuscript.getContent())
                    .build();

            Manuscript manuscript = manuscriptService.updateManuscript(manuscriptDTO, file);
            ManuscriptDTO responseDTO = manuscriptService.convertToDTO(manuscript);

            return ResponseEntity.ok(ApiResponse.success("Manuscript updated successfully", responseDTO));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("File upload failed: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete manuscript
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteManuscript(@PathVariable Long id, HttpSession session) {
        try {
            Long authorId = (Long) session.getAttribute("userId");
            if (authorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            Manuscript manuscript = manuscriptService.getManuscriptById(id);
            if (manuscript == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Manuscript not found"));
            }

            if (!manuscript.getAuthorId().equals(authorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You do not have permission to delete this manuscript"));
            }

            manuscriptService.deleteManuscript(id);
            return ResponseEntity.ok(ApiResponse.success("Manuscript deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
