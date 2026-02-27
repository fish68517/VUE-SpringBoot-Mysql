package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.Result;
import com.studyroom.entity.User;
import com.studyroom.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RankingService {

    private final UserMapper userMapper;

    public RankingService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Result<List<Map<String, Object>>> getMonthlyRanking(int limit) {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .gt(User::getMonthlyStudyTime, 0)
                        .eq(User::getStatus, 1)
                        .orderByDesc(User::getMonthlyStudyTime)
                        .last("LIMIT " + limit)
        );
        return Result.success(toRankingList(users, "monthly"));
    }

    public Result<List<Map<String, Object>>> getTotalRanking(int limit) {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .gt(User::getTotalStudyTime, 0)
                        .eq(User::getStatus, 1)
                        .orderByDesc(User::getTotalStudyTime)
                        .last("LIMIT " + limit)
        );
        return Result.success(toRankingList(users, "total"));
    }

    public Result<Map<String, Object>> getUserRanking(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 获取月排名
        Long monthlyRank = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .gt(User::getMonthlyStudyTime, user.getMonthlyStudyTime())
                        .eq(User::getStatus, 1)
        ) + 1;

        // 获取总排名
        Long totalRank = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .gt(User::getTotalStudyTime, user.getTotalStudyTime())
                        .eq(User::getStatus, 1)
        ) + 1;

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("nickname", user.getNickname());
        result.put("monthlyStudyTime", user.getMonthlyStudyTime());
        result.put("totalStudyTime", user.getTotalStudyTime());
        result.put("monthlyRank", monthlyRank);
        result.put("totalRank", totalRank);

        return Result.success(result);
    }

    private List<Map<String, Object>> toRankingList(List<User> users, String type) {
        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;
        for (User u : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("rank", rank++);
            map.put("userId", u.getId());
            map.put("nickname", u.getNickname() != null ? u.getNickname() : u.getUsername());
            map.put("studyTime", "monthly".equals(type) ? u.getMonthlyStudyTime() : u.getTotalStudyTime());
            map.put("monthlyStudyTime", u.getMonthlyStudyTime());
            map.put("totalStudyTime", u.getTotalStudyTime());
            result.add(map);
        }
        return result;
    }
}
