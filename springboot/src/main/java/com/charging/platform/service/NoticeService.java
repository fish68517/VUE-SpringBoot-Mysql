package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.platform.common.PageResult;
import com.charging.platform.entity.Notice;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public PageResult<Notice> getPublishedNotices(long pageNum, long pageSize) {
        Page<Notice> page = noticeMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getPublishStatus, 1)
                        .orderByDesc(Notice::getPublishTime, Notice::getId));
        return PageResult.from(page);
    }

    public Notice getPublishedNotice(Long id) {
        Notice notice = noticeMapper.selectOne(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getId, id)
                .eq(Notice::getPublishStatus, 1));
        if (notice == null) {
            throw new BusinessException(404, "公告不存在或未发布");
        }
        return notice;
    }
}
