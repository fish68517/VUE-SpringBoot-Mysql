package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.ActivityCreateRequest;
import com.zhuang.embroidery.dto.ActivityResponse;
import com.zhuang.embroidery.dto.ActivityUpdateRequest;
import com.zhuang.embroidery.entity.Activity;
import com.zhuang.embroidery.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;

    /**
     * 创建活动
     *
     * @param request 活动创建请求
     * @return 活动响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public ActivityResponse createActivity(ActivityCreateRequest request) {
        log.info("创建活动: title={}", request.getTitle());

        // 验证参数
        validateActivityRequest(request);

        // 创建新活动
        Activity activity = Activity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .organizer(request.getOrganizer())
                .activityTime(request.getActivityTime())
                .location(request.getLocation())
                .build();

        activity = activityRepository.save(activity);
        log.info("活动创建成功: activityId={}, title={}", activity.getId(), activity.getTitle());

        return ActivityResponse.fromActivity(activity);
    }

    /**
     * 编辑活动
     *
     * @param activityId 活动ID
     * @param request 活动更新请求
     * @return 活动响应
     * @throws IllegalArgumentException 当活动不存在或参数验证失败时抛出
     */
    @Transactional
    public ActivityResponse updateActivity(Long activityId, ActivityUpdateRequest request) {
        log.info("编辑活动: activityId={}", activityId);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> {
                    log.warn("活动不存在: activityId={}", activityId);
                    return new IllegalArgumentException("活动不存在");
                });

        // 验证参数
        validateActivityUpdateRequest(request);

        // 更新字段
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            activity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getOrganizer() != null && !request.getOrganizer().trim().isEmpty()) {
            activity.setOrganizer(request.getOrganizer());
        }
        if (request.getActivityTime() != null && !request.getActivityTime().trim().isEmpty()) {
            activity.setActivityTime(request.getActivityTime());
        }
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            activity.setLocation(request.getLocation());
        }

        activity = activityRepository.save(activity);
        log.info("活动编辑成功: activityId={}", activityId);

        return ActivityResponse.fromActivity(activity);
    }

    /**
     * 删除活动
     *
     * @param activityId 活动ID
     * @throws IllegalArgumentException 当活动不存在时抛出
     */
    @Transactional
    public void deleteActivity(Long activityId) {
        log.info("删除活动: activityId={}", activityId);

        if (!activityRepository.existsById(activityId)) {
            log.warn("活动不存在: activityId={}", activityId);
            throw new IllegalArgumentException("活动不存在");
        }

        activityRepository.deleteById(activityId);
        log.info("活动删除成功: activityId={}", activityId);
    }

    /**
     * 验证活动创建请求参数
     */
    private void validateActivityRequest(ActivityCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("活动标题不能为空");
        }

        if (request.getTitle().length() > 255) {
            throw new IllegalArgumentException("活动标题长度不能超过255个字符");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("活动描述不能为空");
        }
    }

    /**
     * 验证活动更新请求参数
     */
    private void validateActivityUpdateRequest(ActivityUpdateRequest request) {
        if (request.getTitle() != null && request.getTitle().length() > 255) {
            throw new IllegalArgumentException("活动标题长度不能超过255个字符");
        }
    }
}
