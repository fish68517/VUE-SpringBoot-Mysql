package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.CommentCreateRequest;
import com.zhuang.embroidery.dto.CommentListResponse;
import com.zhuang.embroidery.dto.CommentResponse;
import com.zhuang.embroidery.entity.Comment;
import com.zhuang.embroidery.entity.User;
import com.zhuang.embroidery.repository.CommentRepository;
import com.zhuang.embroidery.repository.UserRepository;
import com.zhuang.embroidery.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * 获取评论列表（支持按作品、话题筛选，或获取全站所有评论）
     *
     * @param artworkId 作品ID（可选）
     * @param topicId 话题ID（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论列表响应
     */
    public CommentListResponse getCommentList(Long artworkId, Long topicId, Integer pageNum, Integer pageSize) {
        log.info("获取评论列表: artworkId={}, topicId={}, pageNum={}, pageSize={}", artworkId, topicId, pageNum, pageSize);

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象 (注意 JPA 页码从 0 开始)
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询评论
        Page<Comment> page;
        if (artworkId != null && artworkId > 0) {
            // 如果传了作品 ID，只查该作品下的评论
            page = commentRepository.findByArtworkId(artworkId, pageable);
        } else if (topicId != null && topicId > 0) {
            // 如果传了话题 ID，只查该话题下的评论
            page = commentRepository.findByTopicId(topicId, pageable);
        } else {
            // 🌟 核心修改：如果都不传，不再抛出异常报错，而是直接查询数据库里所有的评论
            page = commentRepository.findAll(pageable);
        }

        // 构建响应
        List<CommentResponse> items = page.getContent()
                .stream()
                .map(comment -> {
                    String username = userRepository.findById(comment.getUserId())
                            .map(User::getUsername) // 假设你的 User 实体有 getUsername 方法
                            .orElse("未知用户");
                    return CommentResponse.fromComment(comment, username);
                })
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return CommentListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 创建评论
     *
     * @param request 评论创建请求
     * @return 评论响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request) {
        log.info("创建评论: userId={}, artworkId={}, topicId={}", request.getUserId(), request.getArtworkId(), request.getTopicId());

        // 验证参数
        validateCommentRequest(request);

        // 验证用户存在
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", request.getUserId());
                    return new IllegalArgumentException("用户不存在");
                });

        // 创建新评论
        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(request.getUserId())
                .artworkId(request.getArtworkId())
                .topicId(request.getTopicId())
                .parentId(request.getParentId())
                .build();

        comment = commentRepository.save(comment);
        log.info("评论创建成功: commentId={}, userId={}", comment.getId(), comment.getUserId());

        return CommentResponse.fromComment(comment, user.getUsername());
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @throws IllegalArgumentException 当评论不存在时抛出
     */
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("删除评论: commentId={}", commentId);

        if (!commentRepository.existsById(commentId)) {
            log.warn("评论不存在: commentId={}", commentId);
            throw new IllegalArgumentException("评论不存在");
        }

        commentRepository.deleteById(commentId);
        log.info("评论删除成功: commentId={}", commentId);
    }

    /**
     * 回复评论（创建一个新的评论作为回复）
     *
     * @param request 评论创建请求（parentId 应该被设置）
     * @return 评论响应
     * @throws IllegalArgumentException 当参数验证失败或父评论不存在时抛出
     */
    @Transactional
    public CommentResponse replyComment(CommentCreateRequest request) {
        log.info("回复评论000: userId={}, parentId={}", request.getUserId(), request.getParentId());

        // 验证参数
        if (request.getParentId() == null || request.getParentId() <= 0) {
            throw new IllegalArgumentException("父评论ID不能为空");
        }

        // 验证父评论存在
        Comment parentComment = commentRepository.findById(request.getParentId())
                .orElseThrow(() -> {
                    log.warn("父评论不存在: parentId={}", request.getParentId());
                    return new IllegalArgumentException("父评论不存在");
                });

        // 验证用户存在
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", request.getUserId());
                    return new IllegalArgumentException("用户不存在");
                });

        // 验证回复内容
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("回复内容不能为空");
        }

        // 创建回复评论（继承父评论的 artworkId 或 topicId）
        Comment reply = Comment.builder()
                .content(request.getContent())
                .userId(request.getUserId())
                .artworkId(parentComment.getArtworkId())
                .topicId(parentComment.getTopicId())
                .parentId(request.getParentId())
                .build();

        reply = commentRepository.save(reply);
        log.info("评论回复成功: replyId={}, parentId={}", reply.getId(), reply.getParentId());

        return CommentResponse.fromComment(reply, user.getUsername());
    }

    /**
     * 获取评论的所有回复
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    public List<CommentResponse> getCommentReplies(Long commentId) {
        log.info("获取评论回复: commentId={}", commentId);

        // 验证评论存在
        if (!commentRepository.existsById(commentId)) {
            log.warn("评论不存在: commentId={}", commentId);
            throw new IllegalArgumentException("评论不存在");
        }

        List<Comment> replies = commentRepository.findByParentId(commentId);

        return replies.stream()
                .map(comment -> {
                    String username = userRepository.findById(comment.getUserId())
                            .map(User::getUsername)
                            .orElse("未知用户");
                    return CommentResponse.fromComment(comment, username);
                })
                .collect(Collectors.toList());
    }

    /**
     * 验证评论创建请求参数
     */
    private void validateCommentRequest(CommentCreateRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }

        if (request.getContent().length() > 5000) {
            throw new IllegalArgumentException("评论内容长度不能超过5000个字符");
        }

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 作品ID和话题ID至少有一个不能为空
        if ((request.getArtworkId() == null || request.getArtworkId() <= 0) &&
            (request.getTopicId() == null || request.getTopicId() <= 0)) {
            throw new IllegalArgumentException("作品ID或话题ID不能为空");
        }
    }

}
