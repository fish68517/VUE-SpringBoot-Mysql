package com.graduation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.entity.UserResourceAction;

public interface UserResourceActionService extends IService<UserResourceAction> {
    // 供 App 端调用的业务方法
    void toggleAction(Long userId, Long resourceId, String type);
}