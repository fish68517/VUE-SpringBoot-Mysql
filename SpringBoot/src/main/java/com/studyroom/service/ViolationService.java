package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.Result;
import com.studyroom.entity.User;
import com.studyroom.entity.Violation;
import com.studyroom.mapper.UserMapper;
import com.studyroom.mapper.ViolationMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ViolationService {

    private final ViolationMapper violationMapper;
    private final UserMapper userMapper;
    private final SystemSettingService settingService;

    public ViolationService(ViolationMapper violationMapper, UserMapper userMapper, SystemSettingService settingService) {
        this.violationMapper = violationMapper;
        this.userMapper = userMapper;
        this.settingService = settingService;
    }

    public void createViolation(Long userId, Long reservationId, Integer type, String description) {
        Violation violation = new Violation();
        violation.setUserId(userId);
        violation.setReservationId(reservationId);
        violation.setType(type);
        violation.setDescription(description);
        violation.setPoints(type == 1 ? 2 : 1); // 预约未到扣2分，其他扣1分
        violation.setStatus(0);
        violationMapper.insert(violation);

        // 检查违规次数是否达到上限
        int violationLimit = settingService.getIntValue("violation_limit", 3);
        int count = violationMapper.countByUserId(userId);
        if (count >= violationLimit) {
            // 禁用用户账号
            User user = userMapper.selectById(userId);
            if (user != null) {
                user.setStatus(0);
                userMapper.updateById(user);
            }
        }
    }

    public Result<List<Map<String, Object>>> getUserViolations(Long userId) {
        List<Violation> violations = violationMapper.selectList(
                new LambdaQueryWrapper<Violation>()
                        .eq(Violation::getUserId, userId)
                        .orderByDesc(Violation::getCreateTime)
        );
        return Result.success(toVOList(violations));
    }

    public Result<List<Map<String, Object>>> getAllViolations() {
        List<Violation> violations = violationMapper.selectList(
                new LambdaQueryWrapper<Violation>()
                        .orderByDesc(Violation::getCreateTime)
        );
        return Result.success(toVOList(violations));
    }

    public Result<String> handleViolation(Long id, Integer status) {
        Violation violation = violationMapper.selectById(id);
        if (violation == null) {
            return Result.error("记录不存在");
        }
        violation.setStatus(status);
        violationMapper.updateById(violation);
        return Result.success("处理成功");
    }

    public Result<Integer> getUserViolationCount(Long userId) {
        int count = violationMapper.countByUserId(userId);
        return Result.success(count);
    }

    private List<Map<String, Object>> toVOList(List<Violation> violations) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Violation v : violations) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", v.getId());
            map.put("userId", v.getUserId());
            map.put("reservationId", v.getReservationId());
            map.put("type", v.getType());
            map.put("typeName", getTypeName(v.getType()));
            map.put("description", v.getDescription());
            map.put("points", v.getPoints());
            map.put("status", v.getStatus());
            map.put("statusName", getStatusName(v.getStatus()));
            map.put("createTime", v.getCreateTime());

            User user = userMapper.selectById(v.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
            }
            result.add(map);
        }
        return result;
    }

    private String getTypeName(Integer type) {
        switch (type) {
            case 1: return "预约未到";
            case 2: return "超时未签退";
            case 3: return "恶意取消";
            default: return "其他";
        }
    }

    private String getStatusName(Integer status) {
        switch (status) {
            case 0: return "未处理";
            case 1: return "已处理";
            case 2: return "已申诉";
            default: return "未知";
        }
    }
}
