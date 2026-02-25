package com.tourism.service;

import com.tourism.entity.Announcement;
import com.tourism.exception.BusinessException;
import com.tourism.repository.AnnouncementRepository;
import com.tourism.util.ValidationUtil;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * 公告业务逻辑服务
 */
@Service
public class AnnouncementService {
    
    private static final Logger logger = LoggerUtil.getLogger(AnnouncementService.class);
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    /**
     * 创建公告
     * @param title 公告标题
     * @param content 公告内容
     * @param createdBy 创建者ID
     * @return 创建的公告对象
     */
    public Announcement createAnnouncement(String title, String content, Long createdBy) {
        // 验证输入参数
        if (title == null || title.trim().isEmpty()) {
            LoggerUtil.warn(logger, "创建公告失败：公告标题不能为空");
            throw new BusinessException("公告标题不能为空");
        }
        
        if (title.length() > 200) {
            LoggerUtil.warn(logger, "创建公告失败：公告标题过长");
            throw new BusinessException("公告标题不能超过200字符");
        }
        
        if (content == null || content.trim().isEmpty()) {
            LoggerUtil.warn(logger, "创建公告失败：公告内容不能为空");
            throw new BusinessException("公告内容不能为空");
        }
        
        if (content.length() > 50000) {
            LoggerUtil.warn(logger, "创建公告失败：公告内容过长");
            throw new BusinessException("公告内容不能超过50000字符");
        }
        
        // 创建公告
        Announcement announcement = new Announcement();
        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        announcement.setCreatedBy(createdBy);
        
        // 保存公告
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        LoggerUtil.info(logger, "创建公告成功 - 标题: " + title + ", 创建者ID: " + createdBy);
        
        return savedAnnouncement;
    }
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告对象
     */
    public Optional<Announcement> findById(Long id) {
        return announcementRepository.findById(id);
    }
    
    /**
     * 获取所有公告（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 公告分页数据
     */
    public Page<Announcement> getAllAnnouncements(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Announcement> announcements = announcementRepository.findAllByOrderByCreatedAtDesc(pageable);
        LoggerUtil.info(logger, "查询公告列表成功 - 页码: " + page + ", 每页数量: " + size);
        return announcements;
    }
    
    /**
     * 更新公告
     * @param id 公告ID
     * @param title 新的公告标题
     * @param content 新的公告内容
     * @return 更新后的公告对象
     */
    public Announcement updateAnnouncement(Long id, String title, String content) {
        Optional<Announcement> announcementOptional = announcementRepository.findById(id);
        if (!announcementOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新公告失败：公告不存在 - ID: " + id);
            throw new BusinessException("公告不存在");
        }
        
        // 验证输入参数
        if (title == null || title.trim().isEmpty()) {
            LoggerUtil.warn(logger, "更新公告失败：公告标题不能为空");
            throw new BusinessException("公告标题不能为空");
        }
        
        if (title.length() > 200) {
            LoggerUtil.warn(logger, "更新公告失败：公告标题过长");
            throw new BusinessException("公告标题不能超过200字符");
        }
        
        if (content == null || content.trim().isEmpty()) {
            LoggerUtil.warn(logger, "更新公告失败：公告内容不能为空");
            throw new BusinessException("公告内容不能为空");
        }
        
        if (content.length() > 50000) {
            LoggerUtil.warn(logger, "更新公告失败：公告内容过长");
            throw new BusinessException("公告内容不能超过50000字符");
        }
        
        Announcement announcement = announcementOptional.get();
        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        
        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        LoggerUtil.info(logger, "更新公告成功 - ID: " + id + ", 标题: " + title);
        
        return updatedAnnouncement;
    }
    
    /**
     * 删除公告
     * @param id 公告ID
     */
    public void deleteAnnouncement(Long id) {
        Optional<Announcement> announcementOptional = announcementRepository.findById(id);
        if (!announcementOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除公告失败：公告不存在 - ID: " + id);
            throw new BusinessException("公告不存在");
        }
        
        announcementRepository.deleteById(id);
        LoggerUtil.info(logger, "删除公告成功 - ID: " + id);
    }
}
