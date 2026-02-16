package com.campus.service;

import com.campus.dto.FeedbackDTO;
import com.campus.entity.Activity;
import com.campus.entity.Feedback;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.FeedbackRepository;
import com.campus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Feedback Service Test
 */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private User testUser;
    private User organizerUser;
    private Activity testActivity;
    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(User.UserRole.USER)
                .build();
        testUser.setId(1L);

        organizerUser = User.builder()
                .username("organizer")
                .email("organizer@example.com")
                .role(User.UserRole.ORGANIZER)
                .build();
        organizerUser.setId(2L);

        testActivity = Activity.builder()
                .title("Test Activity")
                .organizerId(2L)
                .build();
        testActivity.setId(1L);

        testFeedback = Feedback.builder()
                .activityId(1L)
                .userId(1L)
                .rating(5)
                .content("Great activity!")
                .build();
        testFeedback.setId(1L);
    }

    @Test
    void testSubmitFeedback_Success() {
        // Arrange
        FeedbackDTO feedbackDTO = FeedbackDTO.builder()
                .activityId(1L)
                .rating(5)
                .content("Great activity!")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);

        // Act
        FeedbackDTO result = feedbackService.submitFeedback(feedbackDTO, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getRating());
        assertEquals("Great activity!", result.getContent());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void testSubmitFeedback_InvalidRating() {
        // Arrange
        FeedbackDTO feedbackDTO = FeedbackDTO.builder()
                .activityId(1L)
                .rating(6)
                .content("Great activity!")
                .build();

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.submitFeedback(feedbackDTO, "testuser");
        });
    }

    @Test
    void testSubmitFeedback_UserNotFound() {
        // Arrange
        FeedbackDTO feedbackDTO = FeedbackDTO.builder()
                .activityId(1L)
                .rating(5)
                .content("Great activity!")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.submitFeedback(feedbackDTO, "testuser");
        });
    }

    @Test
    void testGetActivityFeedback_Success() {
        // Arrange
        Feedback feedback2 = Feedback.builder()
                .activityId(1L)
                .userId(2L)
                .rating(4)
                .content("Good activity")
                .build();
        feedback2.setId(2L);

        Page<Feedback> feedbackPage = new PageImpl<>(Arrays.asList(testFeedback, feedback2));

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(feedbackRepository.findByActivityId(eq(1L), any(PageRequest.class))).thenReturn(feedbackPage);

        // Act
        Page<FeedbackDTO> result = feedbackService.getActivityFeedback(1L, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(feedbackRepository, times(1)).findByActivityId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetActivityFeedback_ActivityNotFound() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.getActivityFeedback(1L, 0, 10);
        });
    }

    @Test
    void testGetUserFeedback_Success() {
        // Arrange
        Feedback feedback2 = Feedback.builder()
                .activityId(2L)
                .userId(1L)
                .rating(3)
                .content("Average activity")
                .build();
        feedback2.setId(2L);

        Page<Feedback> feedbackPage = new PageImpl<>(Arrays.asList(testFeedback, feedback2));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(feedbackRepository.findByUserId(eq(1L), any(PageRequest.class))).thenReturn(feedbackPage);

        // Act
        Page<FeedbackDTO> result = feedbackService.getUserFeedback("testuser", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(feedbackRepository, times(1)).findByUserId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetAverageRating_Success() {
        // Arrange
        when(feedbackRepository.getAverageRatingByActivityId(1L)).thenReturn(4.5);

        // Act
        Double result = feedbackService.getAverageRating(1L);

        // Assert
        assertEquals(4.5, result);
        verify(feedbackRepository, times(1)).getAverageRatingByActivityId(1L);
    }

    @Test
    void testGetAverageRating_NoFeedback() {
        // Arrange
        when(feedbackRepository.getAverageRatingByActivityId(1L)).thenReturn(null);

        // Act
        Double result = feedbackService.getAverageRating(1L);

        // Assert
        assertEquals(0.0, result);
        verify(feedbackRepository, times(1)).getAverageRatingByActivityId(1L);
    }

    @Test
    void testReplyToFeedback_Success() {
        // Arrange
        String replyContent = "Thank you for your feedback!";

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(organizerUser));

        Feedback repliedFeedback = Feedback.builder()
                .activityId(1L)
                .userId(1L)
                .rating(5)
                .content("Great activity!")
                .replyContent(replyContent)
                .replyBy(2L)
                .build();
        repliedFeedback.setId(1L);

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(repliedFeedback);

        // Act
        FeedbackDTO result = feedbackService.replyToFeedback(1L, replyContent, "organizer");

        // Assert
        assertNotNull(result);
        assertEquals(replyContent, result.getReplyContent());
        assertEquals(2L, result.getReplyBy());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void testReplyToFeedback_EmptyReplyContent() {
        // Arrange
        String emptyReply = "";

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.replyToFeedback(1L, emptyReply, "organizer");
        });
    }

    @Test
    void testReplyToFeedback_ReplyContentTooLong() {
        // Arrange
        String longReply = "a".repeat(1001);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.replyToFeedback(1L, longReply, "organizer");
        });
    }

    @Test
    void testReplyToFeedback_FeedbackNotFound() {
        // Arrange
        String replyContent = "Thank you for your feedback!";

        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.replyToFeedback(1L, replyContent, "organizer");
        });
    }

    @Test
    void testReplyToFeedback_ActivityNotFound() {
        // Arrange
        String replyContent = "Thank you for your feedback!";

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.replyToFeedback(1L, replyContent, "organizer");
        });
    }

    @Test
    void testReplyToFeedback_UserNotOrganizerOfActivity() {
        // Arrange
        String replyContent = "Thank you for your feedback!";

        // Create a different organizer
        User differentOrganizer = User.builder()
                .username("different_organizer")
                .email("different@example.com")
                .role(User.UserRole.ORGANIZER)
                .build();
        differentOrganizer.setId(3L);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(userRepository.findByUsername("different_organizer")).thenReturn(Optional.of(differentOrganizer));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.replyToFeedback(1L, replyContent, "different_organizer");
        });
    }

    @Test
    void testReplyToFeedback_UserNotFound() {
        // Arrange
        String replyContent = "Thank you for your feedback!";

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            feedbackService.replyToFeedback(1L, replyContent, "organizer");
        });
    }

}
