package com.tourism.service;

import com.tourism.entity.Comment;
import com.tourism.exception.BusinessException;
import com.tourism.repository.CommentRepository;
import com.tourism.util.ValidationUtil;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 留言业务逻辑服务
 */
@Service
public class CommentService {
    
    private static final Logger logger = LoggerUtil.getLogger(CommentService.class);
    
    @Autowired
    private CommentRepository commentRepository;
    
    /**
     * 创建留言
     * @param userId 用户ID
     * @param targetType 目标类型（attraction 或 hotel）
     * @param targetId 目标ID
     * @param content 留言内容
     * @param rating 评分（1-5）
     * @return 创建的留言对象
     */
    public Comment createComment(Long userId, String targetType, Long targetId, String content, Integer rating) {
        // 验证输入参数
        if (userId == null || userId <= 0) {
            LoggerUtil.warn(logger, "创建留言失败：用户ID无效 - " + userId);
            throw new BusinessException("用户ID无效");
        }
        
        if (targetType == null || targetType.isEmpty()) {
            LoggerUtil.warn(logger, "创建留言失败：目标类型不能为空");
            throw new BusinessException("目标类型不能为空");
        }
        
        if (!("attraction".equals(targetType) || "hotel".equals(targetType))) {
            LoggerUtil.warn(logger, "创建留言失败：目标类型无效 - " + targetType);
            throw new BusinessException("目标类型无效，只支持 attraction 或 hotel");
        }
        
        if (targetId == null || targetId <= 0) {
            LoggerUtil.warn(logger, "创建留言失败：目标ID无效 - " + targetId);
            throw new BusinessException("目标ID无效");
        }
        
        if (content == null || content.trim().isEmpty()) {
            LoggerUtil.warn(logger, "创建留言失败：留言内容不能为空");
            throw new BusinessException("留言内容不能为空");
        }
        
        if (content.length() > 5000) {
            LoggerUtil.warn(logger, "创建留言失败：留言内容过长");
            throw new BusinessException("留言内容不能超过5000字符");
        }
        
        // 验证评分
        if (rating != null && (rating < 1 || rating > 5)) {
            LoggerUtil.warn(logger, "创建留言失败：评分无效 - " + rating);
            throw new BusinessException("评分必须在1-5之间");
        }
        
        // 创建留言
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setTargetType(targetType);
        comment.setTargetId(targetId);
        comment.setContent(content.trim());
        comment.setRating(rating);
        comment.setStatus("pending");
        comment.setIsPinned(false);
        
        // 保存留言
        Comment savedComment = commentRepository.save(comment);
        LoggerUtil.info(logger, "创建留言成功 - 用户ID: " + userId + ", 目标类型: " + targetType + ", 目标ID: " + targetId);
        
        return savedComment;
    }
    
    /**
     * 根据ID查询留言
     * @param id 留言ID
     * @return 留言对象
     */
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }
    
    /**
     * 根据目标类型和目标ID查询已批准的留言（分页）
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 留言分页数据
     */
    public Page<Comment> getCommentsByTarget(String targetType, Long targetId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByTargetTypeAndTargetIdAndStatus(
            targetType, targetId, "approved", pageable
        );
        LoggerUtil.info(logger, "查询留言成功 - 目标类型: " + targetType + ", 目标ID: " + targetId);
        return comments;
    }
    
    /**
     * 根据用户ID查询留言（分页）
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 留言分页数据
     */
    public Page<Comment> getCommentsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByUserId(userId, pageable);
        LoggerUtil.info(logger, "查询用户留言成功 - 用户ID: " + userId);
        return comments;
    }
    
    /**
     * 根据状态查询留言（分页，管理员）
     * @param status 留言状态
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 留言分页数据
     */
    public Page<Comment> getCommentsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByStatus(status, pageable);
        LoggerUtil.info(logger, "查询留言成功 - 状态: " + status);
        return comments;
    }
    
    /**
     * 获取所有待审核的留言（分页，管理员）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 留言分页数据
     */
    public Page<Comment> getPendingComments(int page, int size) {
        return getCommentsByStatus("pending", page, size);
    }
    
    /**
     * 批准留言（管理员）
     * @param id 留言ID
     * @return 更新后的留言对象
     */
    public Comment approveComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            LoggerUtil.warn(logger, "批准留言失败：留言不存在 - ID: " + id);
            throw new BusinessException("留言不存在");
        }
        
        Comment comment = commentOptional.get();
        
        if ("approved".equals(comment.getStatus())) {
            LoggerUtil.warn(logger, "批准留言失败：留言已被批准 - ID: " + id);
            throw new BusinessException("留言已被批准");
        }
        
        comment.setStatus("approved");
        Comment updatedComment = commentRepository.save(comment);
        LoggerUtil.info(logger, "批准留言成功 - ID: " + id);
        
        return updatedComment;
    }
    
    /**
     * 拒绝留言（管理员）
     * @param id 留言ID
     * @return 更新后的留言对象
     */
    public Comment rejectComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            LoggerUtil.warn(logger, "拒绝留言失败：留言不存在 - ID: " + id);
            throw new BusinessException("留言不存在");
        }
        
        Comment comment = commentOptional.get();
        
        if ("rejected".equals(comment.getStatus())) {
            LoggerUtil.warn(logger, "拒绝留言失败：留言已被拒绝 - ID: " + id);
            throw new BusinessException("留言已被拒绝");
        }
        
        comment.setStatus("rejected");
        Comment updatedComment = commentRepository.save(comment);
        LoggerUtil.info(logger, "拒绝留言成功 - ID: " + id);
        
        return updatedComment;
    }
    
    /**
     * 置顶留言（管理员）
     * @param id 留言ID
     * @return 更新后的留言对象
     */
    public Comment pinComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            LoggerUtil.warn(logger, "置顶留言失败：留言不存在 - ID: " + id);
            throw new BusinessException("留言不存在");
        }
        
        Comment comment = commentOptional.get();
        
        if (comment.getIsPinned()) {
            LoggerUtil.warn(logger, "置顶留言失败：留言已被置顶 - ID: " + id);
            throw new BusinessException("留言已被置顶");
        }
        
        comment.setIsPinned(true);
        Comment updatedComment = commentRepository.save(comment);
        LoggerUtil.info(logger, "置顶留言成功 - ID: " + id);
        
        return updatedComment;
    }
    
    /**
     * 取消置顶留言（管理员）
     * @param id 留言ID
     * @return 更新后的留言对象
     */
    public Comment unpinComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            LoggerUtil.warn(logger, "取消置顶留言失败：留言不存在 - ID: " + id);
            throw new BusinessException("留言不存在");
        }
        
        Comment comment = commentOptional.get();
        
        if (!comment.getIsPinned()) {
            LoggerUtil.warn(logger, "取消置顶留言失败：留言未被置顶 - ID: " + id);
            throw new BusinessException("留言未被置顶");
        }
        
        comment.setIsPinned(false);
        Comment updatedComment = commentRepository.save(comment);
        LoggerUtil.info(logger, "取消置顶留言成功 - ID: " + id);
        
        return updatedComment;
    }
    
    /**
     * 删除留言（管理员）
     * @param id 留言ID
     */
    public void deleteComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除留言失败：留言不存在 - ID: " + id);
            throw new BusinessException("留言不存在");
        }
        
        commentRepository.deleteById(id);
        LoggerUtil.info(logger, "删除留言成功 - ID: " + id);
    }
    
    /**
     * 获取指定目标的置顶留言
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 置顶留言列表
     */
    public List<Comment> getPinnedComments(String targetType, Long targetId) {
        List<Comment> comments = commentRepository.findByTargetTypeAndTargetIdAndStatusAndIsPinned(
            targetType, targetId, "approved", true
        );
        LoggerUtil.info(logger, "查询置顶留言成功 - 目标类型: " + targetType + ", 目标ID: " + targetId);
        return comments;
    }
}
