package com.submission.service;

import com.submission.entity.Manuscript;
import com.submission.entity.Review;
import com.submission.entity.User;
import com.submission.mapper.InitialReviewMapper;
import com.submission.mapper.ManuscriptMapper;
import com.submission.mapper.ReviewMapper;
import com.submission.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Editor Service Test - Tests for editor operations including reviewer assignment
 */
@ExtendWith(MockitoExtension.class)
class EditorServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private ManuscriptMapper manuscriptMapper;

    @Mock
    private InitialReviewMapper initialReviewMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private EditorService editorService;

    private User editor;
    private User reviewer;
    private Manuscript manuscript;

    @BeforeEach
    void setUp() {
        editor = User.builder()
                .id(1L)
                .username("editor1")
                .password("password123")
                .email("editor@example.com")
                .role("EDITOR")
                .status("ACTIVE")
                .build();

        reviewer = User.builder()
                .id(2L)
                .username("reviewer1")
                .password("password123")
                .email("reviewer@example.com")
                .role("REVIEWER")
                .status("ACTIVE")
                .expertiseAreas("Computer Science")
                .researchDirections("AI, Machine Learning")
                .build();

        manuscript = Manuscript.builder()
                .id(1L)
                .authorId(3L)
                .categoryId(1L)
                .title("Test Manuscript")
                .status("UNDER_REVIEW")
                .build();
    }

    @Test
    void testGetAllActiveReviewers() {
        List<User> reviewers = new ArrayList<>();
        reviewers.add(reviewer);

        when(userMapper.findAllActiveReviewers()).thenReturn(reviewers);

        List<User> result = editorService.getAllActiveReviewers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("reviewer1", result.get(0).getUsername());
        verify(userMapper, times(1)).findAllActiveReviewers();
    }

    @Test
    void testAssignReviewerSuccess() {
        when(manuscriptMapper.findById(1L)).thenReturn(manuscript);
        when(userMapper.findById(2L)).thenReturn(reviewer);
        when(reviewMapper.findByManuscriptId(1L)).thenReturn(new ArrayList<>());
        when(reviewMapper.insert(any(Review.class))).thenReturn(1);

        editorService.assignReviewer(1L, 2L, 1L);

        verify(manuscriptMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).findById(2L);
        verify(reviewMapper, times(1)).findByManuscriptId(1L);
        verify(reviewMapper, times(1)).insert(any(Review.class));
    }

    @Test
    void testAssignReviewerManuscriptNotFound() {
        when(manuscriptMapper.findById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> editorService.assignReviewer(1L, 2L, 1L));
        verify(manuscriptMapper, times(1)).findById(1L);
    }

    @Test
    void testAssignReviewerReviewerNotFound() {
        when(manuscriptMapper.findById(1L)).thenReturn(manuscript);
        when(userMapper.findById(2L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> editorService.assignReviewer(1L, 2L, 1L));
        verify(manuscriptMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).findById(2L);
    }

    @Test
    void testAssignReviewerNotReviewerRole() {
        User notReviewer = User.builder()
                .id(2L)
                .username("author1")
                .role("AUTHOR")
                .status("ACTIVE")
                .build();

        when(manuscriptMapper.findById(1L)).thenReturn(manuscript);
        when(userMapper.findById(2L)).thenReturn(notReviewer);

        assertThrows(RuntimeException.class, () -> editorService.assignReviewer(1L, 2L, 1L));
        verify(manuscriptMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).findById(2L);
    }

    @Test
    void testAssignReviewerNotActive() {
        User inactiveReviewer = User.builder()
                .id(2L)
                .username("reviewer1")
                .role("REVIEWER")
                .status("INACTIVE")
                .build();

        when(manuscriptMapper.findById(1L)).thenReturn(manuscript);
        when(userMapper.findById(2L)).thenReturn(inactiveReviewer);

        assertThrows(RuntimeException.class, () -> editorService.assignReviewer(1L, 2L, 1L));
        verify(manuscriptMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).findById(2L);
    }

    @Test
    void testAssignReviewerAlreadyAssigned() {
        Review existingReview = Review.builder()
                .id(1L)
                .manuscriptId(1L)
                .reviewerId(2L)
                .editorId(1L)
                .status("PENDING")
                .build();

        List<Review> existingReviews = new ArrayList<>();
        existingReviews.add(existingReview);

        when(manuscriptMapper.findById(1L)).thenReturn(manuscript);
        when(userMapper.findById(2L)).thenReturn(reviewer);
        when(reviewMapper.findByManuscriptId(1L)).thenReturn(existingReviews);

        assertThrows(RuntimeException.class, () -> editorService.assignReviewer(1L, 2L, 1L));
        verify(manuscriptMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).findById(2L);
        verify(reviewMapper, times(1)).findByManuscriptId(1L);
        verify(reviewMapper, never()).insert(any(Review.class));
    }
}
