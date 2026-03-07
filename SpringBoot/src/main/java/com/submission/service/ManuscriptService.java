package com.submission.service;

import com.submission.dto.InitialReviewDTO;
import com.submission.dto.ManuscriptDTO;
import com.submission.dto.ReviewDTO;
import com.submission.entity.InitialReview;
import com.submission.entity.Manuscript;
import com.submission.entity.Review;
import com.submission.entity.User;
import com.submission.mapper.InitialReviewMapper;
import com.submission.mapper.ManuscriptMapper;
import com.submission.mapper.ReviewMapper;
import com.submission.mapper.UserMapper;
import com.submission.utils.FileUploadUtil;
import com.submission.vo.ManuscriptDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManuscriptService {

    private final ManuscriptMapper manuscriptMapper;
    private final ReviewMapper reviewMapper;
    private final InitialReviewMapper initialReviewMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    /**
     * Submit a new manuscript
     */
    public Manuscript submitManuscript(ManuscriptDTO manuscriptDTO, MultipartFile file) throws IOException {
        // Upload file if provided
        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = FileUploadUtil.uploadFile(file);
        }
        // 判断 filePath 是否包括 image\638cf433-9375-4c26-8192-fe390d35bbbb.pdf 去掉 image\
        if (filePath != null && filePath.contains("image\\")) {
            filePath = filePath.replace("image\\", "");
        }
        System.out.println("filePath: " + filePath);

        // Create manuscript
        Manuscript manuscript = Manuscript.builder()
                .authorId(manuscriptDTO.getAuthorId())
                .categoryId(manuscriptDTO.getCategoryId())
                .title(manuscriptDTO.getTitle())
                .abstractText(manuscriptDTO.getAbstractText())
                .content(manuscriptDTO.getContent())
                .filePath(filePath)
                .status("SUBMITTED")
                .submissionDate(LocalDateTime.now())
                .build();

        manuscriptMapper.insert(manuscript);
        
        // Send notification to author about successful submission
        notificationService.sendManuscriptStatusNotification(manuscriptDTO.getAuthorId(),manuscript.getId(), "DRAFT", "SUBMITTED");
        
        return manuscript;
    }

    /**
     * Get manuscript by id
     */
    public Manuscript getManuscriptById(Long id) {
        return manuscriptMapper.findById(id);
    }

    /**
     * Get all manuscripts by author id
     */
    public List<Manuscript> getManuscriptsByAuthorId(Long authorId) {
        return manuscriptMapper.findByAuthorId(authorId);
    }

    /**
     * Get all manuscripts
     */
    public List<Manuscript> getAllManuscripts() {
        return manuscriptMapper.findAll();
    }

    /**
     * Update manuscript
     */
    public Manuscript updateManuscript(ManuscriptDTO manuscriptDTO, MultipartFile file) throws IOException {
        Manuscript existingManuscript = manuscriptMapper.findById(manuscriptDTO.getId());
        if (existingManuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Check if manuscript can be modified (only DRAFT or SUBMITTED status)
        if (!"DRAFT".equals(existingManuscript.getStatus()) && !"SUBMITTED".equals(existingManuscript.getStatus())) {
            throw new RuntimeException("Only manuscripts in DRAFT or SUBMITTED status can be modified");
        }

        // Upload new file if provided
        String filePath = existingManuscript.getFilePath();
        if (file != null && !file.isEmpty()) {
            // Delete old file if exists
            if (filePath != null) {
                FileUploadUtil.deleteFile(filePath);
            }
            filePath = FileUploadUtil.uploadFile(file);
        }

        // Update manuscript
        Manuscript manuscript = Manuscript.builder()
                .id(manuscriptDTO.getId())
                .authorId(existingManuscript.getAuthorId())
                .categoryId(manuscriptDTO.getCategoryId())
                .title(manuscriptDTO.getTitle())
                .abstractText(manuscriptDTO.getAbstractText())
                .content(manuscriptDTO.getContent())
                .filePath(filePath)
                .status(existingManuscript.getStatus())
                .submissionDate(existingManuscript.getSubmissionDate())
                .build();

        manuscriptMapper.update(manuscript);
        return manuscript;
    }

    /**
     * Delete manuscript
     */
    public void deleteManuscript(Long id) {
        Manuscript manuscript = manuscriptMapper.findById(id);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        // Check if manuscript can be deleted (only DRAFT or SUBMITTED status)
        if (!"DRAFT".equals(manuscript.getStatus()) && !"SUBMITTED".equals(manuscript.getStatus())) {
            throw new RuntimeException("Only manuscripts in DRAFT or SUBMITTED status can be deleted");
        }

        // Delete file if exists
        if (manuscript.getFilePath() != null) {
            FileUploadUtil.deleteFile(manuscript.getFilePath());
        }


        manuscriptMapper.delete(id);
    }

    /**
     * Convert Manuscript entity to ManuscriptDTO
     */
    public ManuscriptDTO convertToDTO(Manuscript manuscript) {
        User author = userMapper.findById(manuscript.getAuthorId());
        String authorName = author != null ? author.getUsername() : "Unknown";

        return ManuscriptDTO.builder()
                .id(manuscript.getId())
                .authorId(manuscript.getAuthorId())
                .authorName(authorName)
                .categoryId(manuscript.getCategoryId())
                .title(manuscript.getTitle())
                .abstractText(manuscript.getAbstractText())
                .content(manuscript.getContent())
                .filePath(manuscript.getFilePath())
                .status(manuscript.getStatus())
                .submissionDate(manuscript.getSubmissionDate())
                .createdAt(manuscript.getCreatedAt())
                .updatedAt(manuscript.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of Manuscript entities to DTOs
     */
    public List<ManuscriptDTO> convertToDTOList(List<Manuscript> manuscripts) {
        return manuscripts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get manuscript detail with review history
     */
    public ManuscriptDetailVO getManuscriptDetail(Long manuscriptId) {
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        ManuscriptDTO manuscriptDTO = convertToDTO(manuscript);
        
        // Get initial review
        InitialReview initialReview = initialReviewMapper.findByManuscriptId(manuscriptId);
        InitialReviewDTO initialReviewDTO = null;
        if (initialReview != null) {
            initialReviewDTO = convertInitialReviewToDTO(initialReview);
        }

        // Get all reviews
        List<Review> reviews = reviewMapper.findByManuscriptId(manuscriptId);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());

        return ManuscriptDetailVO.builder()
                .manuscript(manuscriptDTO)
                .initialReview(initialReviewDTO)
                .reviews(reviewDTOs)
                .build();
    }

    /**
     * Get review history for a manuscript
     */
    public List<ReviewDTO> getReviewHistory(Long manuscriptId) {
        List<Review> reviews = reviewMapper.findByManuscriptId(manuscriptId);
        return reviews.stream()
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Review entity to ReviewDTO
     */
    private ReviewDTO convertReviewToDTO(Review review) {
        User reviewer = userMapper.findById(review.getReviewerId());
        String reviewerName = reviewer != null ? reviewer.getUsername() : "Unknown";

        return ReviewDTO.builder()
                .id(review.getId())
                .manuscriptId(review.getManuscriptId())
                .reviewerId(review.getReviewerId())
                .reviewerName(reviewerName)
                .editorId(review.getEditorId())
                .status(review.getStatus())
                .opinion(review.getOpinion())
                .score(review.getScore())
                .recommendation(review.getRecommendation())
                .submittedDate(review.getSubmittedDate())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    /**
     * Convert InitialReview entity to InitialReviewDTO
     */
    private InitialReviewDTO convertInitialReviewToDTO(InitialReview initialReview) {
        User editor = userMapper.findById(initialReview.getEditorId());
        String editorName = editor != null ? editor.getUsername() : "Unknown";

        return InitialReviewDTO.builder()
                .id(initialReview.getId())
                .manuscriptId(initialReview.getManuscriptId())
                .editorId(initialReview.getEditorId())
                .editorName(editorName)
                .status(initialReview.getStatus())
                .opinion(initialReview.getOpinion())
                .createdAt(initialReview.getCreatedAt())
                .updatedAt(initialReview.getUpdatedAt())
                .build();
    }
}
