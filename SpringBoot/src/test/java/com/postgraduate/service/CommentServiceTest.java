package com.postgraduate.service;

import com.postgraduate.dto.CommentDTO;
import com.postgraduate.entity.Comment;
import com.postgraduate.entity.School;
import com.postgraduate.entity.User;
import com.postgraduate.exception.AuthorizationException;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.repository.CommentRepository;
import com.postgraduate.repository.SchoolRepository;
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
 * Integration test class for CommentService.
 * Tests comment management, deletion, and authorization functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    private User testUser;
    private User adminUser;
    private School testSchool;

    @BeforeEach
    void setUp() {
        // Clear existing data
        commentRepository.deleteAll();
        userRepository.deleteAll();
        schoolRepository.deleteAll();

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

        // Create test school
        testSchool = School.builder()
                .name("Test University")
                .city("Beijing")
                .tier("985")
                .website("http://test.edu.cn")
                .intro("Test school introduction")
                .deleted(false)
                .build();
        testSchool = schoolRepository.save(testSchool);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetSchoolCommentsWithNormalStatus() {
        // Create a comment
        Comment comment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(testUser.getId())
                .content("Test comment")
                .status(Comment.CommentStatus.NORMAL)
                .deleted(false)
                .build();
        commentRepository.save(comment);

        Page<CommentDTO> comments = commentService.getSchoolComments(testSchool.getId(), 0, 20);

        assertNotNull(comments);
        assertEquals(1, comments.getTotalElements());
        assertEquals("Test comment", comments.getContent().get(0).getContent());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetSchoolCommentsExcludesDeletedComments() {
        // Create a normal comment
        Comment normalComment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(testUser.getId())
                .content("Normal comment")
                .status(Comment.CommentStatus.NORMAL)
                .deleted(false)
                .build();
        commentRepository.save(normalComment);

        // Create a deleted comment
        Comment deletedComment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(testUser.getId())
                .content("Deleted comment")
                .status(Comment.CommentStatus.DELETED)
                .deleted(true)
                .build();
        commentRepository.save(deletedComment);

        Page<CommentDTO> comments = commentService.getSchoolComments(testSchool.getId(), 0, 20);

        assertNotNull(comments);
        assertEquals(1, comments.getTotalElements());
        assertEquals("Normal comment", comments.getContent().get(0).getContent());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCreateCommentSuccess() {
        CommentDTO comment = commentService.createComment(testSchool.getId(), "Test comment");

        assertNotNull(comment);
        assertEquals(testSchool.getId(), comment.getSchoolId());
        assertEquals("Test comment", comment.getContent());
        assertEquals(Comment.CommentStatus.NORMAL, comment.getStatus());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCreateCommentWithNonExistentSchool() {
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(999L, "Test comment");
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCreateReplySuccess() {
        // Create parent comment
        Comment parentComment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(testUser.getId())
                .content("Parent comment")
                .status(Comment.CommentStatus.NORMAL)
                .deleted(false)
                .build();
        parentComment = commentRepository.save(parentComment);

        CommentDTO reply = commentService.createReply(parentComment.getId(), "Reply content");

        assertNotNull(reply);
        assertEquals(parentComment.getId(), reply.getParentId());
        assertEquals("Reply content", reply.getContent());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDeleteCommentByAuthor() {
        // Create a comment
        Comment comment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(testUser.getId())
                .content("Test comment")
                .status(Comment.CommentStatus.NORMAL)
                .deleted(false)
                .build();
        comment = commentRepository.save(comment);

        // Delete the comment
        commentService.deleteCommentWithAuthorization(comment.getId());

        // Verify comment is marked as deleted
        Comment deletedComment = commentRepository.findByIdAndDeletedFalse(comment.getId()).orElse(null);
        assertNull(deletedComment);

        // Verify the comment exists but is marked as deleted
        Comment foundComment = commentRepository.findById(comment.getId()).orElse(null);
        assertNotNull(foundComment);
        assertEquals(Comment.CommentStatus.DELETED, foundComment.getStatus());
        assertTrue(foundComment.isDeleted());
    }

    @Test
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testDeleteCommentByAdmin() {
        // Create a comment by testUser
        Comment comment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(testUser.getId())
                .content("Test comment")
                .status(Comment.CommentStatus.NORMAL)
                .deleted(false)
                .build();
        comment = commentRepository.save(comment);

        // Admin deletes the comment
        commentService.deleteCommentWithAuthorization(comment.getId());

        // Verify comment is marked as deleted
        Comment deletedComment = commentRepository.findByIdAndDeletedFalse(comment.getId()).orElse(null);
        assertNull(deletedComment);

        // Verify the comment exists but is marked as deleted
        Comment foundComment = commentRepository.findById(comment.getId()).orElse(null);
        assertNotNull(foundComment);
        assertEquals(Comment.CommentStatus.DELETED, foundComment.getStatus());
        assertTrue(foundComment.isDeleted());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDeleteCommentUnauthorized() {
        // Create a comment by adminUser
        Comment comment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(adminUser.getId())
                .content("Admin comment")
                .status(Comment.CommentStatus.NORMAL)
                .deleted(false)
                .build();
        comment = commentRepository.save(comment);

        // testUser tries to delete the comment
        assertThrows(AuthorizationException.class, () -> {
            commentService.deleteCommentWithAuthorization(comment.getId());
        });

        // Verify comment is still NORMAL and not deleted
        Comment foundComment = commentRepository.findById(comment.getId()).orElse(null);
        assertNotNull(foundComment);
        assertEquals(Comment.CommentStatus.NORMAL, foundComment.getStatus());
        assertFalse(foundComment.isDeleted());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDeleteNonExistentComment() {
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteCommentWithAuthorization(999L);
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDeleteAlreadyDeletedComment() {
        // Create and delete a comment
        Comment comment = Comment.builder()
                .schoolId(testSchool.getId())
                .userId(testUser.getId())
                .content("Test comment")
                .status(Comment.CommentStatus.DELETED)
                .deleted(true)
                .build();
        comment = commentRepository.save(comment);

        // Try to delete it again
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteCommentWithAuthorization(comment.getId());
        });
    }
}
