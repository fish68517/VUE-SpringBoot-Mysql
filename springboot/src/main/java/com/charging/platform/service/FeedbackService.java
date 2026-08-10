package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.platform.common.PageResult;
import com.charging.platform.dto.FeedbackRequest;
import com.charging.platform.entity.SysUser;
import com.charging.platform.entity.UserFeedback;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.SysUserMapper;
import com.charging.platform.mapper.UserFeedbackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final UserFeedbackMapper feedbackMapper;
    private final SysUserMapper userMapper;

    public PageResult<UserFeedback> getFeedbacks(Long userId, long pageNum, long pageSize) {
        requireUser(userId);
        Page<UserFeedback> page = feedbackMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<UserFeedback>()
                        .eq(UserFeedback::getUserId, userId)
                        .orderByDesc(UserFeedback::getCreateTime, UserFeedback::getId));
        return PageResult.from(page);
    }

    @Transactional
    public UserFeedback submit(FeedbackRequest request) {
        requireUser(request.getUserId());
        UserFeedback feedback = new UserFeedback();
        feedback.setUserId(request.getUserId());
        feedback.setFeedbackType(request.getFeedbackType());
        feedback.setTitle(request.getTitle().trim());
        feedback.setContent(request.getContent().trim());
        feedback.setContact(StringUtils.hasText(request.getContact()) ? request.getContact().trim() : null);
        feedback.setProcessStatus(0);
        feedbackMapper.insert(feedback);
        return feedbackMapper.selectById(feedback.getId());
    }

    private void requireUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
    }
}
