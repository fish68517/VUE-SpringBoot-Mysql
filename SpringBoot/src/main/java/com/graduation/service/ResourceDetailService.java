package com.graduation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.entity.ResourceDetail;

public interface ResourceDetailService extends IService<ResourceDetail> {
    // 可以在此定义特定业务方法，如处理点赞
    void toggleLike(Long userId, Long resourceId);
    void toggleCollect(Long userId, Long resourceId);
}