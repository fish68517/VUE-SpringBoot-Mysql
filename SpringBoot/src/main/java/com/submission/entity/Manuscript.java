package com.submission.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Manuscript Entity - Represents a manuscript submission
 *
 *  DRAFT（草稿）→ SUBMITTED（已提交）→ UNDER_REVIEW（审核中）
 *                      ↓
 * （审核结果）→ REVISION_REQUIRED（需修改）→ 重新提交 → UNDER_REVIEW（再次审核）
 *                      ↓
 * 最终结果：ACCEPTED（录用） / REJECTED（拒稿）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manuscript {
    private Long id;
    private Long authorId;
    private Long categoryId;
    private String title;
    private String abstractText;
    private String content;
    private String filePath;
    private String status; // DRAFT, SUBMITTED, UNDER_REVIEW, REVISION_REQUIRED, ACCEPTED, REJECTED
    private LocalDateTime submissionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
