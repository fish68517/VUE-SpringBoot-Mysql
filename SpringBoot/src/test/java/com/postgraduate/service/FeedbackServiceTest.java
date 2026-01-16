package com.postgraduate.service;

import com.postgraduate.dto.FeedbackDTO;
import com.postgraduate.entity.Feedback;
import com.postgraduate.entity.User;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.repository.FeedbackRepository;
import com.postgraduate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for FeedbackService.
 * Tests feedback submission, retrieval, and admin management functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FeedbackServiceTest {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Clear existing data
        feedbackRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .username("testuser")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        testUser = userRepository.save(testUser);

        // Create admin user
        adminUser = User.builder()
                .username("adminuser")
                .passwordHash("hashedpassword")
                .role(User.UserRole.ADMIN)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        adminUser = userRepository.save(adminUser);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSubmitFeedbackSuccess() {
        FeedbackDTO feedback = feedbackService.submitFeedback("BUG", "Test bug report");

        assertNotNull(feedback);
        assertEquals("BUG", feedback.getType());
        assertEquals("Test bug report", feedback.getContent());
        assertEquals("NEW", feedback.getStatus());
        assertNull(feedback.getAdminReply());
        assertNull(feedback.getRepliedAt());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSubmitFeedbackWithDifferentTypes() {
        FeedbackDTO bugFeedback = feedbackService.submitFeedback("BUG", "Bug content");
        FeedbackDTO suggestionFeedback = feedbackService.submitFeedback("SUGGESTION", "Suggestion content");
        FeedbackDTO dataErrorFeedback = feedbackService.submitFeedback("DATA_ERROR", "Data error content");

        assertEquals("BUG", bugFeedback.getType());
        assertEquals("SUGGESTION", suggestionFeedback.getType());
        assertEquals("DATA_ERROR", dataErrorFeedback.getType());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSubmitFeedbackWithInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.submitFeedback("INVALID_TYPE", "Test content");
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserFeedback() {
        // Submit multiple feedback
        feedbackService.submitFeedback("BUG", "Bug 1");
        feedbackService.submitFeedback("SUGGESTION", "Suggestion 1");
        feedbackService.submitFeedback("DATA_ERROR", "Data error 1");

        Page<FeedbackDTO> feedback = feedbackService.getUserFeedback(0, 20);

        assertNotNull(feedback);
        assertEquals(3, feedback.getTotalElements());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserFeedbackWithPagination() {
        // Submit 5 feedback
        for (int i = 0; i < 5; i++) {
            feedbackService.submitFeedback("BUG", "Bug " + i);
        }

        Page<FeedbackDTO> page1 = feedbackService.getUserFeedback(0, 2);
        Page<FeedbackDTO> page2 = feedbackService.getUserFeedback(1, 2);

        assertEquals(2, page1.getContent().size());
        assertEquals(2, page2.getContent().size());
        assertEquals(5, page1.getTotalElements());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserFeedbackDefaultPageSize() {
        // Submit 25 feedback
        for (int i = 0; i < 25; i++) {
            feedbackService.submitFeedback("BUG", "Bug " + i);
        }

        Page<FeedbackDTO> feedback = feedbackService.getUserFeedback(0, null);

        assertEquals(20, feedback.getContent().size()); // Default page size is 20
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testGetAdminFeedbackNoFilters() {
        // Create feedback with different statuses and types
        Feedback feedback1 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug 1")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback1);

        Feedback feedback2 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.SUGGESTION)
                .content("Suggestion 1")
                .status(Feedback.FeedbackStatus.PROCESSING)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback2);

        Page<FeedbackDTO> feedback = feedbackService.getAdminFeedback(null, null, 0, 20);

        assertNotNull(feedback);
        assertEquals(2, feedback.getTotalElements());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testGetAdminFeedbackFilterByStatus() {
        // Create feedback with different statuses
        Feedback feedback1 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug 1")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback1);

        Feedback feedback2 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug 2")
                .status(Feedback.FeedbackStatus.PROCESSING)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback2);

        Page<FeedbackDTO> feedback = feedbackService.getAdminFeedback("NEW", null, 0, 20);

        assertNotNull(feedback);
        assertEquals(1, feedback.getTotalElements());
        assertEquals("NEW", feedback.getContent().get(0).getStatus());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testGetAdminFeedbackFilterByType() {
        // Create feedback with different types
        Feedback feedback1 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug 1")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback1);

        Feedback feedback2 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.SUGGESTION)
                .content("Suggestion 1")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback2);

        Page<FeedbackDTO> feedback = feedbackService.getAdminFeedback(null, "BUG", 0, 20);

        assertNotNull(feedback);
        assertEquals(1, feedback.getTotalElements());
        assertEquals("BUG", feedback.getContent().get(0).getType());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testGetAdminFeedbackFilterByStatusAndType() {
        // Create feedback with different combinations
        Feedback feedback1 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug 1")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback1);

        Feedback feedback2 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug 2")
                .status(Feedback.FeedbackStatus.PROCESSING)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback2);

        Feedback feedback3 = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.SUGGESTION)
                .content("Suggestion 1")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedbackRepository.save(feedback3);

        Page<FeedbackDTO> feedback = feedbackService.getAdminFeedback("NEW", "BUG", 0, 20);

        assertNotNull(feedback);
        assertEquals(1, feedback.getTotalElements());
        assertEquals("BUG", feedback.getContent().get(0).getType());
        assertEquals("NEW", feedback.getContent().get(0).getStatus());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testGetAdminFeedbackWithInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.getAdminFeedback("INVALID_STATUS", null, 0, 20);
        });
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testGetAdminFeedbackWithInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.getAdminFeedback(null, "INVALID_TYPE", 0, 20);
        });
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testReplyToFeedbackSuccess() {
        // Create feedback
        Feedback feedback = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug report")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedback = feedbackRepository.save(feedback);

        // Admin replies
        FeedbackDTO updatedFeedback = feedbackService.replyToFeedback(feedback.getId(), "PROCESSING", "We are investigating this issue");

        assertNotNull(updatedFeedback);
        assertEquals("PROCESSING", updatedFeedback.getStatus());
        assertEquals("We are investigating this issue", updatedFeedback.getAdminReply());
        assertNotNull(updatedFeedback.getRepliedAt());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testReplyToFeedbackWithDoneStatus() {
        // Create feedback
        Feedback feedback = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug report")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedback = feedbackRepository.save(feedback);

        // Admin marks as done
        FeedbackDTO updatedFeedback = feedbackService.replyToFeedback(feedback.getId(), "DONE", "Issue has been fixed");

        assertNotNull(updatedFeedback);
        assertEquals("DONE", updatedFeedback.getStatus());
        assertEquals("Issue has been fixed", updatedFeedback.getAdminReply());
        assertNotNull(updatedFeedback.getRepliedAt());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testReplyToNonExistentFeedback() {
        assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.replyToFeedback(999L, "PROCESSING", "Reply");
        });
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testReplyToFeedbackWithInvalidStatus() {
        // Create feedback
        Feedback feedback = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug report")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedback = feedbackRepository.save(feedback);

        assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.replyToFeedback(feedback.getId(), "INVALID_STATUS", "Reply");
        });
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testReplyToFeedbackMultipleTimes() {
        // Create feedback
        Feedback feedback = Feedback.builder()
                .userId(testUser.getId())
                .type(Feedback.FeedbackType.BUG)
                .content("Bug report")
                .status(Feedback.FeedbackStatus.NEW)
                .deleted(false)
                .build();
        feedback = feedbackRepository.save(feedback);

        // First reply
        FeedbackDTO firstReply = feedbackService.replyToFeedback(feedback.getId(), "PROCESSING", "Investigating");
        assertEquals("PROCESSING", firstReply.getStatus());

        // Second reply
        FeedbackDTO secondReply = feedbackService.replyToFeedback(feedback.getId(), "DONE", "Fixed");
        assertEquals("DONE", secondReply.getStatus());
        assertEquals("Fixed", secondReply.getAdminReply());
    }
}
