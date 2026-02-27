package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.Result;
import com.studyroom.entity.Feedback;
import com.studyroom.entity.Seat;
import com.studyroom.entity.User;
import com.studyroom.mapper.FeedbackMapper;
import com.studyroom.mapper.SeatMapper;
import com.studyroom.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final UserMapper userMapper;
    private final SeatMapper seatMapper;

    public FeedbackService(FeedbackMapper feedbackMapper, UserMapper userMapper, SeatMapper seatMapper) {
        this.feedbackMapper = feedbackMapper;
        this.userMapper = userMapper;
        this.seatMapper = seatMapper;
    }

    public Result<String> createFeedback(Long userId, Feedback feedback) {
        feedback.setUserId(userId);
        feedback.setStatus(0);
        feedbackMapper.insert(feedback);
        return Result.success("反馈提交成功");
    }

    public Result<List<Map<String, Object>>> getUserFeedbacks(Long userId) {
        List<Feedback> feedbacks = feedbackMapper.selectList(
                new LambdaQueryWrapper<Feedback>()
                        .eq(Feedback::getUserId, userId)
                        .orderByDesc(Feedback::getCreateTime)
        );
        return Result.success(toVOList(feedbacks));
    }

    public Result<List<Map<String, Object>>> getAllFeedbacks(Integer status) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<Feedback>()
                .orderByAsc(Feedback::getStatus)
                .orderByDesc(Feedback::getCreateTime);
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        List<Feedback> feedbacks = feedbackMapper.selectList(wrapper);
        return Result.success(toVOList(feedbacks));
    }

    public Result<String> replyFeedback(Long id, String reply, Long replyUserId) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }
        feedback.setReply(reply);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setReplyUserId(replyUserId);
        feedback.setStatus(2); // 已解决
        feedbackMapper.updateById(feedback);
        return Result.success("回复成功");
    }

    public Result<String> updateFeedbackStatus(Long id, Integer status) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }
        feedback.setStatus(status);
        feedbackMapper.updateById(feedback);
        return Result.success("状态更新成功");
    }

    private List<Map<String, Object>> toVOList(List<Feedback> feedbacks) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Feedback f : feedbacks) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("userId", f.getUserId());
            map.put("seatId", f.getSeatId());
            map.put("type", f.getType());
            map.put("typeName", getTypeName(f.getType()));
            map.put("content", f.getContent());
            map.put("images", f.getImages());
            map.put("status", f.getStatus());
            map.put("statusName", getStatusName(f.getStatus()));
            map.put("reply", f.getReply());
            map.put("replyTime", f.getReplyTime());
            map.put("createTime", f.getCreateTime());

            User user = userMapper.selectById(f.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
            }

            if (f.getSeatId() != null) {
                Seat seat = seatMapper.selectById(f.getSeatId());
                if (seat != null) {
                    map.put("seatNo", seat.getSeatNo());
                    map.put("seatArea", seat.getArea());
                }
            }

            result.add(map);
        }
        return result;
    }

    private String getTypeName(Integer type) {
        switch (type) {
            case 1: return "座位问题";
            case 2: return "环境问题";
            case 3: return "建议";
            case 4: return "其他";
            default: return "未知";
        }
    }

    private String getStatusName(Integer status) {
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "已解决";
            case 3: return "已关闭";
            default: return "未知";
        }
    }
}
