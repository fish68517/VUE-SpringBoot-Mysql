package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.TopicCreateRequest;
import com.zhuang.embroidery.dto.TopicListResponse;
import com.zhuang.embroidery.dto.TopicResponse;
import com.zhuang.embroidery.dto.TopicUpdateRequest;
import com.zhuang.embroidery.entity.Topic;
import com.zhuang.embroidery.entity.User;
import com.zhuang.embroidery.repository.TopicRepository;
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
 * 话题业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    /**
     * 获取话题列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 话题列表响应
     */
    public TopicListResponse getTopicList(Integer pageNum, Integer pageSize) {
        log.info("获取话题列表: pageNum={}, pageSize={}", pageNum, pageSize);

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询话题列表（置顶话题优先）
        List<Topic> pinnedTopics = topicRepository.findByIsPinnedTrue();
        Page<Topic> unpinnedPage = topicRepository.findByIsPinnedFalse(pageable);

        // 构建响应
        List<TopicResponse> items = pinnedTopics.stream()
                .map(topic -> {
                    String creatorUsername = userRepository.findById(topic.getCreatedBy())
                            .map(User::getUsername)
                            .orElse("未知用户");
                    return TopicResponse.fromTopic(topic, creatorUsername);
                })
                .collect(Collectors.toList());

        // 添加未置顶的话题
        items.addAll(unpinnedPage.getContent().stream()
                .map(topic -> {
                    String creatorUsername = userRepository.findById(topic.getCreatedBy())
                            .map(User::getUsername)
                            .orElse("未知用户");
                    return TopicResponse.fromTopic(topic, creatorUsername);
                })
                .collect(Collectors.toList()));

        pageUtil.setTotal(pinnedTopics.size() + unpinnedPage.getTotalElements());
        pageUtil.calculateTotalPages();

        return TopicListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 获取话题详情
     *
     * @param topicId 话题ID
     * @return 话题响应
     * @throws IllegalArgumentException 当话题不存在时抛出
     */
    public TopicResponse getTopicDetail(Long topicId) {
        log.info("获取话题详情: topicId={}", topicId);

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("话题不存在: topicId={}", topicId);
                    return new IllegalArgumentException("话题不存在");
                });

        String creatorUsername = userRepository.findById(topic.getCreatedBy())
                .map(User::getUsername)
                .orElse("未知用户");

        return TopicResponse.fromTopic(topic, creatorUsername);
    }

    /**
     * 创建话题
     *
     * @param request 话题创建请求
     * @return 话题响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public TopicResponse createTopic(TopicCreateRequest request) {
        log.info("创建话题: title={}, createdBy={}", request.getTitle(), request.getCreatedBy());

        // 验证参数
        validateTopicRequest(request);

        // 验证创建者存在
        User creator = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> {
                    log.warn("创建者不存在: createdBy={}", request.getCreatedBy());
                    return new IllegalArgumentException("创建者不存在");
                });

        // 创建新话题
        Topic topic = Topic.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .isPinned(false)
                .build();

        topic = topicRepository.save(topic);
        log.info("话题创建成功: topicId={}, title={}", topic.getId(), topic.getTitle());

        return TopicResponse.fromTopic(topic, creator.getUsername());
    }

    /**
     * 编辑话题
     *
     * @param topicId 话题ID
     * @param request 话题更新请求
     * @return 话题响应
     * @throws IllegalArgumentException 当话题不存在或参数验证失败时抛出
     */
    @Transactional
    public TopicResponse updateTopic(Long topicId, TopicUpdateRequest request) {
        log.info("编辑话题: topicId={}", topicId);

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("话题不存在: topicId={}", topicId);
                    return new IllegalArgumentException("话题不存在");
                });

        // 验证参数
        validateTopicUpdateRequest(request);

        // 更新字段
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            topic.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            topic.setDescription(request.getDescription());
        }

        topic = topicRepository.save(topic);
        log.info("话题编辑成功: topicId={}", topicId);

        String creatorUsername = userRepository.findById(topic.getCreatedBy())
                .map(User::getUsername)
                .orElse("未知用户");

        return TopicResponse.fromTopic(topic, creatorUsername);
    }

    /**
     * 删除话题
     *
     * @param topicId 话题ID
     * @throws IllegalArgumentException 当话题不存在时抛出
     */
    @Transactional
    public void deleteTopic(Long topicId) {
        log.info("删除话题: topicId={}", topicId);

        if (!topicRepository.existsById(topicId)) {
            log.warn("话题不存在: topicId={}", topicId);
            throw new IllegalArgumentException("话题不存在");
        }

        topicRepository.deleteById(topicId);
        log.info("话题删除成功: topicId={}", topicId);
    }

    /**
     * 置顶话题
     *
     * @param topicId 话题ID
     * @return 话题响应
     * @throws IllegalArgumentException 当话题不存在时抛出
     */
    @Transactional
    public TopicResponse pinTopic(Long topicId) {
        log.info("置顶话题: topicId={}", topicId);

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("话题不存在: topicId={}", topicId);
                    return new IllegalArgumentException("话题不存在");
                });

        topic.setIsPinned(true);
        topic = topicRepository.save(topic);
        log.info("话题置顶成功: topicId={}", topicId);

        String creatorUsername = userRepository.findById(topic.getCreatedBy())
                .map(User::getUsername)
                .orElse("未知用户");

        return TopicResponse.fromTopic(topic, creatorUsername);
    }

    /**
     * 取消置顶话题
     *
     * @param topicId 话题ID
     * @return 话题响应
     * @throws IllegalArgumentException 当话题不存在时抛出
     */
    @Transactional
    public TopicResponse unpinTopic(Long topicId) {
        log.info("取消置顶话题: topicId={}", topicId);

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("话题不存在: topicId={}", topicId);
                    return new IllegalArgumentException("话题不存在");
                });

        topic.setIsPinned(false);
        topic = topicRepository.save(topic);
        log.info("话题取消置顶成功: topicId={}", topicId);

        String creatorUsername = userRepository.findById(topic.getCreatedBy())
                .map(User::getUsername)
                .orElse("未知用户");

        return TopicResponse.fromTopic(topic, creatorUsername);
    }

    /**
     * 验证话题创建请求参数
     */
    private void validateTopicRequest(TopicCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("话题标题不能为空");
        }

        if (request.getTitle().length() > 255) {
            throw new IllegalArgumentException("话题标题长度不能超过255个字符");
        }

        if (request.getCreatedBy() == null || request.getCreatedBy() <= 0) {
            throw new IllegalArgumentException("创建者ID不能为空");
        }
    }

    /**
     * 验证话题更新请求参数
     */
    private void validateTopicUpdateRequest(TopicUpdateRequest request) {
        if (request.getTitle() != null && request.getTitle().length() > 255) {
            throw new IllegalArgumentException("话题标题长度不能超过255个字符");
        }
    }

}
