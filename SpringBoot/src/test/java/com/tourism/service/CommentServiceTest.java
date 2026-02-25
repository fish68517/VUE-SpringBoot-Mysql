package com.tourism.service;

import com.tourism.entity.Comment;
import com.tourism.exception.BusinessException;
import com.tourism.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 留言服务单元测试
 */
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    
    @Mock
    private CommentRepository commentRepository;
    
    @InjectMocks
    private CommentService commentService;
    
    @Test
    public void testCreateCommentSuccess() {
        // 准备测试数据
        Long userId = 1L;
        String targetType = "attraction";
        Long targetId = 1L;
        String content = "这是一个很好的景点，值得一去！";
        Integer rating = 5;
        
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setUserId(userId);
        mockComment.setTargetType(targetType);
        mockComment.setTargetId(targetId);
        mockComment.setContent(content);
        mockComment.setRating(rating);
        mockComment.setStatus("pending");
        mockComment.setIsPinned(false);
        mockComment.setCreatedAt(LocalDateTime.now());
        mockComment.setUpdatedAt(LocalDateTime.now());
        
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);
        
        // 执行创建留言
        Comment result = commentService.createComment(userId, targetType, targetId, content, rating);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(targetType, result.getTargetType());
        assertEquals(targetId, result.getTargetId());
        assertEquals(content, result.getContent());
        assertEquals(rating, result.getRating());
        assertEquals("pending", result.getStatus());
        assertFalse(result.getIsPinned());
        
        // 验证save方法被调用
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    public void testCreateCommentWithInvalidUserId() {
        // 验证用户ID无效时抛出异常
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(null, "attraction", 1L, "content", 5);
        });
        
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(0L, "attraction", 1L, "content", 5);
        });
        
        // 验证save方法未被调用
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    public void testCreateCommentWithInvalidTargetType() {
        // 验证目标类型无效时抛出异常
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, null, 1L, "content", 5);
        });
        
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, "invalid", 1L, "content", 5);
        });
        
        // 验证save方法未被调用
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    public void testCreateCommentWithInvalidTargetId() {
        // 验证目标ID无效时抛出异常
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, "attraction", null, "content", 5);
        });
        
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, "attraction", 0L, "content", 5);
        });
        
        // 验证save方法未被调用
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    public void testCreateCommentWithEmptyContent() {
        // 验证内容为空时抛出异常
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, "attraction", 1L, null, 5);
        });
        
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, "attraction", 1L, "", 5);
        });
        
        // 验证save方法未被调用
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    public void testCreateCommentWithInvalidRating() {
        // 验证评分无效时抛出异常
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, "attraction", 1L, "content", 0);
        });
        
        assertThrows(BusinessException.class, () -> {
            commentService.createComment(1L, "attraction", 1L, "content", 6);
        });
        
        // 验证save方法未被调用
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    public void testGetCommentsByTargetSuccess() {
        // 准备测试数据
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setUserId(1L);
        comment1.setTargetType("attraction");
        comment1.setTargetId(1L);
        comment1.setContent("很好的景点");
        comment1.setRating(5);
        comment1.setStatus("approved");
        comment1.setIsPinned(false);
        
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setUserId(2L);
        comment2.setTargetType("attraction");
        comment2.setTargetId(1L);
        comment2.setContent("不错的地方");
        comment2.setRating(4);
        comment2.setStatus("approved");
        comment2.setIsPinned(false);
        
        List<Comment> commentList = Arrays.asList(comment1, comment2);
        Page<Comment> mockPage = new PageImpl<>(commentList, PageRequest.of(0, 10), 2);
        
        when(commentRepository.findByTargetTypeAndTargetIdAndStatus(
            "attraction", 1L, "approved", PageRequest.of(0, 10)))
            .thenReturn(mockPage);
        
        // 执行查询
        Page<Comment> result = commentService.getCommentsByTarget("attraction", 1L, 0, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        
        // 验证查询方法被调用
        verify(commentRepository, times(1)).findByTargetTypeAndTargetIdAndStatus(
            "attraction", 1L, "approved", PageRequest.of(0, 10));
    }
    
    @Test
    public void testApproveCommentSuccess() {
        // 准备测试数据
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setUserId(1L);
        mockComment.setTargetType("attraction");
        mockComment.setTargetId(1L);
        mockComment.setContent("很好的景点");
        mockComment.setRating(5);
        mockComment.setStatus("pending");
        mockComment.setIsPinned(false);
        
        Comment approvedComment = new Comment();
        approvedComment.setId(1L);
        approvedComment.setUserId(1L);
        approvedComment.setTargetType("attraction");
        approvedComment.setTargetId(1L);
        approvedComment.setContent("很好的景点");
        approvedComment.setRating(5);
        approvedComment.setStatus("approved");
        approvedComment.setIsPinned(false);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(approvedComment);
        
        // 执行批准留言
        Comment result = commentService.approveComment(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("approved", result.getStatus());
        
        // 验证save方法被调用
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    public void testApproveCommentWithNonexistentComment() {
        // 设置留言不存在
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            commentService.approveComment(999L);
        });
        
        // 验证save方法未被调用
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    public void testRejectCommentSuccess() {
        // 准备测试数据
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setUserId(1L);
        mockComment.setTargetType("attraction");
        mockComment.setTargetId(1L);
        mockComment.setContent("很好的景点");
        mockComment.setRating(5);
        mockComment.setStatus("pending");
        mockComment.setIsPinned(false);
        
        Comment rejectedComment = new Comment();
        rejectedComment.setId(1L);
        rejectedComment.setUserId(1L);
        rejectedComment.setTargetType("attraction");
        rejectedComment.setTargetId(1L);
        rejectedComment.setContent("很好的景点");
        rejectedComment.setRating(5);
        rejectedComment.setStatus("rejected");
        rejectedComment.setIsPinned(false);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(rejectedComment);
        
        // 执行拒绝留言
        Comment result = commentService.rejectComment(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("rejected", result.getStatus());
        
        // 验证save方法被调用
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    public void testPinCommentSuccess() {
        // 准备测试数据
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setUserId(1L);
        mockComment.setTargetType("attraction");
        mockComment.setTargetId(1L);
        mockComment.setContent("很好的景点");
        mockComment.setRating(5);
        mockComment.setStatus("approved");
        mockComment.setIsPinned(false);
        
        Comment pinnedComment = new Comment();
        pinnedComment.setId(1L);
        pinnedComment.setUserId(1L);
        pinnedComment.setTargetType("attraction");
        pinnedComment.setTargetId(1L);
        pinnedComment.setContent("很好的景点");
        pinnedComment.setRating(5);
        pinnedComment.setStatus("approved");
        pinnedComment.setIsPinned(true);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(pinnedComment);
        
        // 执行置顶留言
        Comment result = commentService.pinComment(1L);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.getIsPinned());
        
        // 验证save方法被调用
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    public void testUnpinCommentSuccess() {
        // 准备测试数据
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setUserId(1L);
        mockComment.setTargetType("attraction");
        mockComment.setTargetId(1L);
        mockComment.setContent("很好的景点");
        mockComment.setRating(5);
        mockComment.setStatus("approved");
        mockComment.setIsPinned(true);
        
        Comment unpinnedComment = new Comment();
        unpinnedComment.setId(1L);
        unpinnedComment.setUserId(1L);
        unpinnedComment.setTargetType("attraction");
        unpinnedComment.setTargetId(1L);
        unpinnedComment.setContent("很好的景点");
        unpinnedComment.setRating(5);
        unpinnedComment.setStatus("approved");
        unpinnedComment.setIsPinned(false);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(unpinnedComment);
        
        // 执行取消置顶留言
        Comment result = commentService.unpinComment(1L);
        
        // 验证结果
        assertNotNull(result);
        assertFalse(result.getIsPinned());
        
        // 验证save方法被调用
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    public void testDeleteCommentSuccess() {
        // 准备测试数据
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setUserId(1L);
        mockComment.setTargetType("attraction");
        mockComment.setTargetId(1L);
        mockComment.setContent("很好的景点");
        mockComment.setRating(5);
        mockComment.setStatus("approved");
        mockComment.setIsPinned(false);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));
        
        // 执行删除留言
        commentService.deleteComment(1L);
        
        // 验证deleteById方法被调用
        verify(commentRepository, times(1)).deleteById(1L);
    }
    
    @Test
    public void testDeleteCommentWithNonexistentComment() {
        // 设置留言不存在
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            commentService.deleteComment(999L);
        });
        
        // 验证deleteById方法未被调用
        verify(commentRepository, never()).deleteById(any());
    }
    
    @Test
    public void testGetPinnedCommentsSuccess() {
        // 准备测试数据
        Comment pinnedComment = new Comment();
        pinnedComment.setId(1L);
        pinnedComment.setUserId(1L);
        pinnedComment.setTargetType("attraction");
        pinnedComment.setTargetId(1L);
        pinnedComment.setContent("很好的景点");
        pinnedComment.setRating(5);
        pinnedComment.setStatus("approved");
        pinnedComment.setIsPinned(true);
        
        List<Comment> pinnedComments = Arrays.asList(pinnedComment);
        
        when(commentRepository.findByTargetTypeAndTargetIdAndStatusAndIsPinned(
            "attraction", 1L, "approved", true))
            .thenReturn(pinnedComments);
        
        // 执行查询置顶留言
        List<Comment> result = commentService.getPinnedComments("attraction", 1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsPinned());
        
        // 验证查询方法被调用
        verify(commentRepository, times(1)).findByTargetTypeAndTargetIdAndStatusAndIsPinned(
            "attraction", 1L, "approved", true);
    }
}
