package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.UserResourceAction;
import com.graduation.mapper.UserResourceActionMapper;
import com.graduation.service.UserResourceActionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserResourceActionServiceImpl extends ServiceImpl<UserResourceActionMapper, UserResourceAction> implements UserResourceActionService {

    @Override
    @Transactional
    public void toggleAction(Long userId, Long resourceId, String type) {
        // 1. 查询是否存在记录
        QueryWrapper<UserResourceAction> query = new QueryWrapper<>();
        query.eq("campus_user_id", userId).eq("learn_resource_id", resourceId);
        UserResourceAction action = this.getOne(query);

        // 2. 如果不存在，初始化一条
        if (action == null) {
            action = new UserResourceAction();
            action.setCampusUserId(userId);
            action.setLearnResourceId(resourceId);
            action.setIsLiked(false);
            action.setIsCollected(false);
        }

        // 3. 根据操作类型切换状态
        if ("like".equals(type)) {
            action.setIsLiked(!action.getIsLiked());
        } else if ("collect".equals(type)) {
            action.setIsCollected(!action.getIsCollected());
        }

        // 4. 保存或更新
        this.saveOrUpdate(action);
    }
}