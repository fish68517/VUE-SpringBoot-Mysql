package com.submission.service;

import com.submission.dto.UserDTO;
import com.submission.dto.ReviewDTO;
import com.submission.entity.User;
import com.submission.entity.Review;
import com.submission.mapper.UserMapper;
import com.submission.mapper.ReviewMapper;
import com.submission.mapper.ManuscriptMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Reviewer Service Test - Tests for reviewer operations
 */
@ExtendWith(MockitoExtension.class)
class ReviewerServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ManuscriptMapper manuscriptMapper;

    @InjectMocks
    private ReviewerService reviewerService;

    private User reviewer;
    private UserDTO reviewerDTO;

    @BeforeEach
    void setUp() {
        reviewer = User.builder()
                .id(1L)
                .username("reviewer1")
                .password("password123")
                .email("reviewer@example.com")
                .phone("1234567890")
                .role("REVIEWER")
                .status("ACTIVE")
                .expertiseAreas("Computer Science, AI")
                .researchDirections("Machine Learning, NLP")
                .build();

        reviewerDTO = UserDTO.builder()
                .expertiseAreas("Computer Science, AI, Data Science")
                .researchDirections("Machine Learning, NLP, Deep Learning")
                .build();
    }

    @Test
    void testGetReviewerInfo() {
        when(userMapper.findById(1L)).thenReturn(reviewer);

        User result = reviewerService.getReviewerInfo(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("REVIEWER", result.getRole());
        assertEquals("reviewer1", result.getUsername());
        verify(userMapper, times(1)).findById(1L);
    }

    @Test
    void testGetReviewerInfoNotFound() {
        when(userMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> reviewerService.getReviewerInfo(999L));
        verify(userMapper, times(1)).findById(999L);
    }

    @Test
    void testGetReviewerInfoNotReviewer() {
        User author = User.builder()
                .id(2L)
                .username("author1")
                .role("AUTHOR")
                .build();

        when(userMapper.findById(2L)).thenReturn(author);

        assertThrows(RuntimeException.class, () -> reviewerService.getReviewerInfo(2L));
        verify(userMapper, times(1)).findById(2L);
    }

    @Test
    void testUpdateReviewerExpertise() {
        when(userMapper.findById(1L)).thenReturn(reviewer);
        when(userMapper.update(any(User.class))).thenReturn(1);

        User result = reviewerService.updateReviewerExpertise(1L, reviewerDTO);

        assertNotNull(result);
        assertEquals("Computer Science, AI, Data Science", result.getExpertiseAreas());
        assertEquals("Machine Learning, NLP, Deep Learning", result.getResearchDirections());
        verify(userMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).update(any(User.class));
    }

    @Test
    void testUpdateReviewerExpertiseNotFound() {
        when(userMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> reviewerService.updateReviewerExpertise(999L, reviewerDTO));
        verify(userMapper, times(1)).findById(999L);
        verify(userMapper, never()).update(any(User.class));
    }

    @Test
    void testUpdateReviewerExpertiseNotReviewer() {
        User author = User.builder()
                .id(2L)
                .username("author1")
                .role("AUTHOR")
                .build();

        when(userMapper.findById(2L)).thenReturn(author);

        assertThrows(RuntimeException.class, () -> reviewerService.updateReviewerExpertise(2L, reviewerDTO));
        verify(userMapper, times(1)).findById(2L);
        verify(userMapper, never()).update(any(User.class));
    }

    @Test
    void testUpdateReviewerPasswordSuccess() {
        when(userMapper.findById(1L)).thenReturn(reviewer);
        when(userMapper.update(any(User.class))).thenReturn(1);

        reviewerService.updateReviewerPassword(1L, "password123", "newpassword123");

        verify(userMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).update(any(User.class));
    }

    @Test
    void testUpdateReviewerPasswordNotFound() {
        when(userMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> reviewerService.updateReviewerPassword(999L, "password123", "newpassword123"));
        verify(userMapper, times(1)).findById(999L);
        verify(userMapper, never()).update(any(User.class));
    }

    @Test
    void testUpdateReviewerPasswordNotReviewer() {
        User author = User.builder()
                .id(2L)
                .username("author1")
                .role("AUTHOR")
                .build();

        when(userMapper.findById(2L)).thenReturn(author);

        assertThrows(RuntimeException.class, () -> reviewerService.updateReviewerPassword(2L, "password123", "newpassword123"));
        verify(userMapper, times(1)).findById(2L);
        verify(userMapper, never()).update(any(User.class));
    }

    @Test
    void testUpdateReviewerPasswordIncorrectOldPassword() {
        when(userMapper.findById(1L)).thenReturn(reviewer);

        assertThrows(RuntimeException.class, () -> reviewerService.updateReviewerPassword(1L, "wrongpassword", "newpassword123"));
        verify(userMapper, times(1)).findById(1L);
        verify(userMapper, never()).update(any(User.class));
    }

    @Test
    void testGetReviewHistory() {
        when(userMapper.findById(1L)).thenReturn(reviewer);
        
        Review review1 = Review.builder()
                .id(1L)
                .manuscriptId(10L)
                .reviewerId(1L)
                .editorId(2L)
                .status("SUBMITTED")
                .opinion("Good paper")
                .score(85)
                .recommendation("ACCEPT")
                .submittedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .id(2L)
                .manuscriptId(11L)
                .reviewerId(1L)
                .editorId(2L)
                .status("SUBMITTED")
                .opinion("Needs revision")
                .score(70)
                .recommendation("ACCEPT_WITH_REVISION")
                .submittedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reviewMapper.findByReviewerId(1L)).thenReturn(Arrays.asList(review1, review2));

        List<ReviewDTO> history = reviewerService.getReviewHistory(1L);

        assertNotNull(history);
        assertEquals(2, history.size());
        verify(userMapper, atLeast(1)).findById(1L);
        verify(reviewMapper, times(1)).findByReviewerId(1L);
    }

    @Test
    void testGetReviewHistoryNotFound() {
        when(userMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> reviewerService.getReviewHistory(999L));
        verify(userMapper, times(1)).findById(999L);
        verify(reviewMapper, never()).findByReviewerId(any());
    }

    @Test
    void testGetReviewOpinionDetails() {
        Review review = Review.builder()
                .id(1L)
                .manuscriptId(10L)
                .reviewerId(1L)
                .editorId(2L)
                .status("SUBMITTED")
                .opinion("Good paper")
                .score(85)
                .recommendation("ACCEPT")
                .submittedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reviewMapper.findById(1L)).thenReturn(review);
        when(userMapper.findById(1L)).thenReturn(reviewer);

        ReviewDTO result = reviewerService.getReviewOpinionDetails(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Good paper", result.getOpinion());
        assertEquals(85, result.getScore());
        assertEquals("ACCEPT", result.getRecommendation());
        verify(reviewMapper, times(1)).findById(1L);
    }

    @Test
    void testGetReviewOpinionDetailsNotFound() {
        when(reviewMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> reviewerService.getReviewOpinionDetails(999L, 1L));
        verify(reviewMapper, times(1)).findById(999L);
    }

    @Test
    void testGetReviewOpinionDetailsNotOwner() {
        Review review = Review.builder()
                .id(1L)
                .manuscriptId(10L)
                .reviewerId(2L)
                .editorId(3L)
                .status("SUBMITTED")
                .opinion("Good paper")
                .score(85)
                .recommendation("ACCEPT")
                .build();

        when(reviewMapper.findById(1L)).thenReturn(review);

        assertThrows(RuntimeException.class, () -> reviewerService.getReviewOpinionDetails(1L, 1L));
        verify(reviewMapper, times(1)).findById(1L);
    }
}
