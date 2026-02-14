package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.AccessStatisticsResponse;
import com.zhuang.embroidery.dto.SystemSettingsResponse;
import com.zhuang.embroidery.dto.SystemSettingsUpdateRequest;
import com.zhuang.embroidery.entity.SystemSettings;
import com.zhuang.embroidery.repository.ArtworkRepository;
import com.zhuang.embroidery.repository.CommentRepository;
import com.zhuang.embroidery.repository.FeedbackRepository;
import com.zhuang.embroidery.repository.KnowledgeRepository;
import com.zhuang.embroidery.repository.OperationLogRepository;
import com.zhuang.embroidery.repository.SystemSettingsRepository;
import com.zhuang.embroidery.repository.TopicRepository;
import com.zhuang.embroidery.repository.UserRepository;
import com.zhuang.embroidery.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemService {

    private final SystemSettingsRepository systemSettingsRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final VoteRepository voteRepository;
    private final FeedbackRepository feedbackRepository;
    private final OperationLogRepository operationLogRepository;

    /**
     * 获取系统设置
     *
     * @return 系统设置响应
     * @throws IllegalArgumentException 当系统设置不存在时抛出
     */
    public SystemSettingsResponse getSystemSettings() {
        log.info("获取系统设置");

        SystemSettings settings = systemSettingsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("系统设置不存在");
                    return new IllegalArgumentException("系统设置不存在");
                });

        return SystemSettingsResponse.fromSystemSettings(settings);
    }

    /**
     * 获取系统设置（根据ID）
     *
     * @param id 系统设置ID
     * @return 系统设置响应
     * @throws IllegalArgumentException 当系统设置不存在时抛出
     */
    public SystemSettingsResponse getSystemSettingsById(Long id) {
        log.info("获取系统设置: id={}", id);

        SystemSettings settings = systemSettingsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("系统设置不存在: id={}", id);
                    return new IllegalArgumentException("系统设置不存在");
                });

        return SystemSettingsResponse.fromSystemSettings(settings);
    }

    /**
     * 更新系统设置
     *
     * @param request 系统设置更新请求
     * @return 系统设置响应
     * @throws IllegalArgumentException 当系统设置不存在时抛出
     */
    @Transactional
    public SystemSettingsResponse updateSystemSettings(SystemSettingsUpdateRequest request) {
        log.info("更新系统设置");

        // 获取第一条系统设置记录
        SystemSettings settings = systemSettingsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("系统设置不存在");
                    return new IllegalArgumentException("系统设置不存在");
                });

        // 更新字段
        if (request.getSiteName() != null && !request.getSiteName().trim().isEmpty()) {
            settings.setSiteName(request.getSiteName());
        }

        if (request.getSiteDescription() != null) {
            settings.setSiteDescription(request.getSiteDescription());
        }

        if (request.getLogo() != null) {
            settings.setLogo(request.getLogo());
        }

        if (request.getContactEmail() != null) {
            settings.setContactEmail(request.getContactEmail());
        }

        if (request.getContactPhone() != null) {
            settings.setContactPhone(request.getContactPhone());
        }

        settings = systemSettingsRepository.save(settings);
        log.info("系统设置更新成功: id={}", settings.getId());

        return SystemSettingsResponse.fromSystemSettings(settings);
    }

    /**
     * 更新系统设置（根据ID）
     *
     * @param id 系统设置ID
     * @param request 系统设置更新请求
     * @return 系统设置响应
     * @throws IllegalArgumentException 当系统设置不存在时抛出
     */
    @Transactional
    public SystemSettingsResponse updateSystemSettingsById(Long id, SystemSettingsUpdateRequest request) {
        log.info("更新系统设置: id={}", id);

        SystemSettings settings = systemSettingsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("系统设置不存在: id={}", id);
                    return new IllegalArgumentException("系统设置不存在");
                });

        // 更新字段
        if (request.getSiteName() != null && !request.getSiteName().trim().isEmpty()) {
            settings.setSiteName(request.getSiteName());
        }

        if (request.getSiteDescription() != null) {
            settings.setSiteDescription(request.getSiteDescription());
        }

        if (request.getLogo() != null) {
            settings.setLogo(request.getLogo());
        }

        if (request.getContactEmail() != null) {
            settings.setContactEmail(request.getContactEmail());
        }

        if (request.getContactPhone() != null) {
            settings.setContactPhone(request.getContactPhone());
        }

        settings = systemSettingsRepository.save(settings);
        log.info("系统设置更新成功: id={}", settings.getId());

        return SystemSettingsResponse.fromSystemSettings(settings);
    }

    /**
     * 获取访问统计数据
     *
     * @return 访问统计响应
     */
    public AccessStatisticsResponse getAccessStatistics() {
        log.info("获取访问统计数据");

        // 统计各类数据
        Long totalVisits = operationLogRepository.count();
        Long totalUsers = userRepository.count();
        Long totalArtworks = artworkRepository.count();
        Long totalKnowledge = knowledgeRepository.count();
        Long totalComments = commentRepository.count();
        Long totalTopics = topicRepository.count();
        Long totalVotes = voteRepository.count();
        Long totalFeedback = feedbackRepository.count();

        log.info("访问统计数据: 总访问量={}, 总用户数={}, 总作品数={}, 总知识数={}, 总评论数={}, 总话题数={}, 总投票数={}, 总反馈数={}",
                totalVisits, totalUsers, totalArtworks, totalKnowledge, totalComments, totalTopics, totalVotes, totalFeedback);

        return AccessStatisticsResponse.builder()
                .totalVisits(totalVisits)
                .totalUsers(totalUsers)
                .totalArtworks(totalArtworks)
                .totalKnowledge(totalKnowledge)
                .totalComments(totalComments)
                .totalTopics(totalTopics)
                .totalVotes(totalVotes)
                .totalFeedback(totalFeedback)
                .build();
    }

}
