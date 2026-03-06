package com.submission.vo;

import com.submission.dto.InitialReviewDTO;
import com.submission.dto.ManuscriptDTO;
import com.submission.dto.ReviewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Manuscript Detail View Object - Contains manuscript info with review history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManuscriptDetailVO {
    private ManuscriptDTO manuscript;
    private InitialReviewDTO initialReview;
    private List<ReviewDTO> reviews;
}
