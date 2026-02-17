package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.FeedbackCreateRequest;
import com.zhuang.embroidery.dto.FeedbackListResponse;
import com.zhuang.embroidery.dto.FeedbackResponse;
import com.zhuang.embroidery.entity.Feedback;
import com.zhuang.embroidery.entity.User;
import com.zhuang.embroidery.repository.FeedbackRepository;
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
 * 反馈业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    /**
     * 提交反馈
     *
     * @param request 反馈创建请求
     * @return 反馈响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public FeedbackResponse submitFeedback(FeedbackCreateRequest request) {
        log.info("提交反馈: userId={}", request.getUserId());

        // 验证参数
        validateFeedbackRequest(request);

        // 验证用户存在
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", request.getUserId());
                    return new IllegalArgumentException("用户不存在");
                });

        // 创建新反馈
        Feedback feedback = Feedback.builder()
                .userId(request.getUserId())
                .content(request.getContent())
                .email(request.getEmail())
                .build();

        feedback = feedbackRepository.save(feedback);
        log.info("反馈提交成功: feedbackId={}, userId={}", feedback.getId(), feedback.getUserId());

        return FeedbackResponse.fromFeedback(feedback, user.getUsername());
    }

    /**
     * 获取反馈列表（管理员）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 反馈列表响应
     */
    public FeedbackListResponse getFeedbackList(Integer pageNum, Integer pageSize) {
        log.info("获取反馈列表: pageNum={}, pageSize={}", pageNum, pageSize);

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询反馈
        Page<Feedback> page = feedbackRepository.findAll(pageable);

        // 构建响应
        List<FeedbackResponse> items = page.getContent()
                .stream()
                .map(feedback -> {
                    String username = userRepository.findById(feedback.getUserId())
                            .map(User::getUsername)
                            .orElse("未知用户");
                    return FeedbackResponse.fromFeedback(feedback, username);
                })
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return FeedbackListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 获取用户的反馈列表
     *
     * @param userId 用户ID
     * @return 反馈列表
     */
    public List<FeedbackResponse> getUserFeedbackList(Long userId) {
        log.info("获取用户反馈列表: userId={}", userId);

        // 验证用户存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId);
                    return new IllegalArgumentException("用户不存在");
                });

        List<Feedback> feedbackList = feedbackRepository.findByUserId(userId);

        return feedbackList.stream()
                .map(feedback -> FeedbackResponse.fromFeedback(feedback, user.getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * 获取待处理反馈列表（管理员）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 反馈列表响应
     */
    public FeedbackListResponse getPendingFeedbackList(Integer pageNum, Integer pageSize) {
        log.info("获取待处理反馈列表: pageNum={}, pageSize={}", pageNum, pageSize);

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询待处理反馈
        Page<Feedback> page = feedbackRepository.findByStatus("pending", pageable);

        // 构建响应
        List<FeedbackResponse> items = page.getContent()
                .stream()
                .map(feedback -> {
                    String username = userRepository.findById(feedback.getUserId())
                            .map(User::getUsername)
                            .orElse("未知用户");
                    return FeedbackResponse.fromFeedback(feedback, username);
                })
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return FeedbackListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }


    /**
     * 处理反馈（将状态从 pending 改为 processed）
     *
     * @param feedbackId 反馈ID
     * @throws IllegalArgumentException 当反馈不存在或已经处理时抛出
     */
    @Transactional // 开启事务保证数据一致性
    public void processFeedback(Long feedbackId) {
        log.info("准备处理反馈: feedbackId={}", feedbackId);

        // 1. 查询反馈是否存在
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> {
                    log.warn("反馈不存在: feedbackId={}", feedbackId);
                    return new IllegalArgumentException("反馈不存在");
                });

        // 2. 检查状态是否已经是“已处理”
        if ("processed".equals(feedback.getStatus())) {
            log.warn("反馈已处理过，无需重复操作: feedbackId={}", feedbackId);
            throw new IllegalArgumentException("该反馈已经处理过了，请刷新页面");
        }

        // 3. 修改状态并保存
        feedback.setStatus("processed");
        feedbackRepository.save(feedback);

        log.info("反馈处理成功: feedbackId={}", feedbackId);
    }

    /**
     * 删除反馈（可选功能，以防管理员需要清空垃圾反馈）
     *
     * @param feedbackId 反馈ID
     */
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        log.info("删除反馈: feedbackId={}", feedbackId);

        if (!feedbackRepository.existsById(feedbackId)) {
            log.warn("待删除的反馈不存在: feedbackId={}", feedbackId);
            throw new IllegalArgumentException("反馈不存在");
        }

        feedbackRepository.deleteById(feedbackId);
        log.info("反馈删除成功: feedbackId={}", feedbackId);
    }

    /**
     * 验证反馈创建请求参数
     */
    private void validateFeedbackRequest(FeedbackCreateRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("反馈内容不能为空");
        }

        if (request.getContent().length() > 5000) {
            throw new IllegalArgumentException("反馈内容长度不能超过5000个字符");
        }

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // 简单的邮箱格式验证
            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
        }
    }

}
