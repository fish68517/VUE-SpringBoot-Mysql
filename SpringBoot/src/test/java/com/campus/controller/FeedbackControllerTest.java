package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.FeedbackDTO;
import com.campus.service.FeedbackService;
import com.campus.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Feedback Controller Test
 */
@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private FeedbackController feedbackController;

    private MockMvc mockMvc;
    private String validToken;
    private FeedbackDTO testFeedbackDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController).build();
        validToken = "Bearer valid_token";

        testFeedbackDTO = FeedbackDTO.builder()
                .id(1L)
                .userId(1L)
                .activityId(1L)
                .rating(5)
                .content("Great activity!")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testSubmitFeedback_Success() {
        // Arrange
        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("testuser");
        when(feedbackService.submitFeedback(any(FeedbackDTO.class), eq("testuser"))).thenReturn(testFeedbackDTO);

        // Act
        ResponseEntity<ApiResponse<FeedbackDTO>> response = feedbackController.submitFeedback(validToken, testFeedbackDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Feedback submitted successfully", response.getBody().getMessage());
        assertEquals(testFeedbackDTO, response.getBody().getData());
        verify(feedbackService, times(1)).submitFeedback(any(FeedbackDTO.class), eq("testuser"));
    }

    @Test
    void testSubmitFeedback_InvalidToken() {
        // Arrange
        String invalidToken = "Bearer invalid_token";

        // Act
        ResponseEntity<ApiResponse<FeedbackDTO>> response = feedbackController.submitFeedback(invalidToken, testFeedbackDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

    @Test
    void testGetActivityFeedback_Success() {
        // Arrange
        FeedbackDTO feedback2 = FeedbackDTO.builder()
                .id(2L)
                .userId(2L)
                .activityId(1L)
                .rating(4)
                .content("Good activity")
                .createdAt(LocalDateTime.now())
                .build();

        Page<FeedbackDTO> feedbackPage = new PageImpl<>(Arrays.asList(testFeedbackDTO, feedback2));

        when(feedbackService.getActivityFeedback(1L, 0, 10)).thenReturn(feedbackPage);

        // Act
        ResponseEntity<ApiResponse<Page<FeedbackDTO>>> response = feedbackController.getActivityFeedback(1L, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Feedback retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().getContent().size());
        verify(feedbackService, times(1)).getActivityFeedback(1L, 0, 10);
    }

    @Test
    void testGetMyFeedback_Success() {
        // Arrange
        FeedbackDTO feedback2 = FeedbackDTO.builder()
                .id(2L)
                .userId(1L)
                .activityId(2L)
                .rating(3)
                .content("Average activity")
                .createdAt(LocalDateTime.now())
                .build();

        Page<FeedbackDTO> feedbackPage = new PageImpl<>(Arrays.asList(testFeedbackDTO, feedback2));

        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("testuser");
        when(feedbackService.getUserFeedback("testuser", 0, 10)).thenReturn(feedbackPage);

        // Act
        ResponseEntity<ApiResponse<Page<FeedbackDTO>>> response = feedbackController.getMyFeedback(validToken, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Feedback retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().getContent().size());
        verify(feedbackService, times(1)).getUserFeedback("testuser", 0, 10);
    }

    @Test
    void testGetMyFeedback_InvalidToken() {
        // Arrange
        String invalidToken = "Bearer invalid_token";

        // Act
        ResponseEntity<ApiResponse<Page<FeedbackDTO>>> response = feedbackController.getMyFeedback(invalidToken, 0, 10);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

    @Test
    void testGetAverageRating_Success() {
        // Arrange
        when(feedbackService.getAverageRating(1L)).thenReturn(4.5);

        // Act
        ResponseEntity<ApiResponse<Double>> response = feedbackController.getAverageRating(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Average rating retrieved successfully", response.getBody().getMessage());
        assertEquals(4.5, response.getBody().getData());
        verify(feedbackService, times(1)).getAverageRating(1L);
    }

    @Test
    void testReplyToFeedback_Success() {
        // Arrange
        FeedbackDTO repliedFeedback = FeedbackDTO.builder()
                .id(1L)
                .userId(1L)
                .activityId(1L)
                .rating(5)
                .content("Great activity!")
                .replyContent("Thank you for your feedback!")
                .replyBy(2L)
                .createdAt(LocalDateTime.now())
                .build();

        FeedbackController.ReplyRequest replyRequest = new FeedbackController.ReplyRequest();
        replyRequest.setReplyContent("Thank you for your feedback!");

        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("organizer");
        when(feedbackService.replyToFeedback(1L, "Thank you for your feedback!", "organizer")).thenReturn(repliedFeedback);

        // Act
        ResponseEntity<ApiResponse<FeedbackDTO>> response = feedbackController.replyToFeedback(1L, validToken, replyRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Reply submitted successfully", response.getBody().getMessage());
        assertEquals(repliedFeedback, response.getBody().getData());
        assertEquals("Thank you for your feedback!", response.getBody().getData().getReplyContent());
        verify(feedbackService, times(1)).replyToFeedback(1L, "Thank you for your feedback!", "organizer");
    }

    @Test
    void testReplyToFeedback_InvalidToken() {
        // Arrange
        String invalidToken = "Bearer invalid_token";
        FeedbackController.ReplyRequest replyRequest = new FeedbackController.ReplyRequest();
        replyRequest.setReplyContent("Thank you for your feedback!");

        // Act
        ResponseEntity<ApiResponse<FeedbackDTO>> response = feedbackController.replyToFeedback(1L, invalidToken, replyRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

    @Test
    void testReplyToFeedback_NoToken() {
        // Arrange
        FeedbackController.ReplyRequest replyRequest = new FeedbackController.ReplyRequest();
        replyRequest.setReplyContent("Thank you for your feedback!");

        // Act
        ResponseEntity<ApiResponse<FeedbackDTO>> response = feedbackController.replyToFeedback(1L, null, replyRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

}
