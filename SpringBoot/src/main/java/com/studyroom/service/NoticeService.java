package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.Result;
import com.studyroom.entity.Notice;
import com.studyroom.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    public Result<List<Notice>> getPublishedNotices() {
        List<Notice> notices = noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 1)
                        .orderByDesc(Notice::getType)
                        .orderByDesc(Notice::getPublishTime)
        );
        return Result.success(notices);
    }

    public Result<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>()
                        .orderByDesc(Notice::getCreateTime)
        );
        return Result.success(notices);
    }

    public Result<Notice> getNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            return Result.error("公告不存在");
        }
        return Result.success(notice);
    }

    public Result<String> createNotice(Notice notice, Long userId) {
        notice.setCreateUserId(userId);
        notice.setPublishTime(LocalDateTime.now());
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        if (notice.getType() == null) {
            notice.setType(0);
        }
        noticeMapper.insert(notice);
        return Result.success("发布成功");
    }

    public Result<String> updateNotice(Notice notice) {
        Notice existing = noticeMapper.selectById(notice.getId());
        if (existing == null) {
            return Result.error("公告不存在");
        }
        noticeMapper.updateById(notice);
        return Result.success("更新成功");
    }

    public Result<String> deleteNotice(Long id) {
        noticeMapper.deleteById(id);
        return Result.success("删除成功");
    }

    public Result<String> updateNoticeStatus(Long id, Integer status) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            return Result.error("公告不存在");
        }
        notice.setStatus(status);
        if (status == 1) {
            notice.setPublishTime(LocalDateTime.now());
        }
        noticeMapper.updateById(notice);
        return Result.success(status == 1 ? "已发布" : "已下架");
    }
}
