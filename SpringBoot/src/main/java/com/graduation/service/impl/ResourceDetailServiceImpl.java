package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.ResourceDetail;
import com.graduation.mapper.ResourceDetailMapper;
import com.graduation.service.ResourceDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// 注意：这里简化了UserResourceAction的处理，实际项目建议为Action表单独建Entity/Mapper
// 为了演示方便，这里仅更新Detail表的计数，实际应同时更新Action表状态

@Service
public class ResourceDetailServiceImpl extends ServiceImpl<ResourceDetailMapper, ResourceDetail> implements ResourceDetailService {

    @Override
    @Transactional
    public void toggleLike(Long userId, Long resourceId) {
        // 简单实现：找到记录并+1。实际应检查用户是否已赞，防止重复。
        ResourceDetail detail = this.getOne(new QueryWrapper<ResourceDetail>().eq("learn_resource_id", resourceId));
        if (detail != null) {
            detail.setLikeCount(detail.getLikeCount() + 1);
            this.updateById(detail);
        }
    }

    @Override
    @Transactional
    public void toggleCollect(Long userId, Long resourceId) {
        ResourceDetail detail = this.getOne(new QueryWrapper<ResourceDetail>().eq("learn_resource_id", resourceId));
        if (detail != null) {
            detail.setCollectCount(detail.getCollectCount() + 1);
            this.updateById(detail);
        }
    }
}