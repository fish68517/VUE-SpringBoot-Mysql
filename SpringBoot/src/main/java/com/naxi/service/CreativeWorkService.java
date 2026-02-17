package com.naxi.service;

import com.naxi.common.BusinessException;
import com.naxi.entity.CreativeWork;
import com.naxi.entity.User;
import com.naxi.repository.CreativeWorkRepository;
import com.naxi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreativeWorkService {
    @Autowired
    private CreativeWorkRepository creativeWorkRepository;

    @Autowired
    private UserRepository userRepository;


    // src/main/java/com/naxi/service/CreativeWorkService.java

    public CreativeWork saveWork(CreativeWork work) {
        // 修复核心逻辑：
        // 即使前端传了 userId，也必须将对应的 User 实体对象注入到 work 中
        if (work.getUserId() != null) {
            User user = userRepository.findById(work.getUserId())
                    .orElseThrow(() -> new BusinessException("用户不存在，无法关联作品"));
            work.setUser(user); // 这一步解决了 not-null property 报错
        }

        // 设置其他必要字段
        work.setStatus(CreativeWork.WorkStatus.PENDING);
        work.setCreatedAt(LocalDateTime.now());
        work.setUpdatedAt(LocalDateTime.now());

        return creativeWorkRepository.save(work);
    }


    /**
     * 上传原创作品
     */
    public CreativeWork uploadCreativeWork(CreativeWork creativeWork) {
        // 验证用户是否存在
        System.out.println("上传作品时的 userId: " + creativeWork.getUserId());
        Optional<User> user = userRepository.findById(creativeWork.getUserId());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 设置初始状态为待审核
        creativeWork.setStatus(CreativeWork.WorkStatus.PENDING);
        creativeWork.setLikeCount(0);
        creativeWork.setCreatedAt(LocalDateTime.now());
        creativeWork.setUpdatedAt(LocalDateTime.now());

        creativeWork.setUser(user.get()); // 关联 User 实体对象
        return creativeWorkRepository.save(creativeWork);
    }

    /**
     * 获取原创作品列表（分页）
     */
    public Page<CreativeWork> getCreativeWorks(Pageable pageable) {
        // 只返回已审核通过的作品
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.APPROVED, pageable);
    }

    /**
     * 获取用户的原创作品列表（分页）
     */
    public Page<CreativeWork> getUserCreativeWorks(Long userId, Pageable pageable) {
        return creativeWorkRepository.findByUserId(userId, pageable);
    }

    /**
     * 获取作品详情
     */
    public CreativeWork getCreativeWorkById(Long id) {
        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        if (creativeWork.isPresent()) {
            return creativeWork.get();
        }
        return null;
    }

    /**
     * 删除作品
     */
    public void deleteCreativeWork(Long id) {
        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        if (creativeWork.isPresent()) {
            creativeWorkRepository.deleteById(id);
        }
    }

    /**
     * 获取待审核作品列表（管理员）
     */
    public Page<CreativeWork> getPendingCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.PENDING, pageable);
    }

    /**
     * 审核作品（管理员，通过/拒绝）
     */
    public CreativeWork reviewCreativeWork(Long id, CreativeWork.WorkStatus status) {
        // 只允许通过或拒绝状态
        if (status != CreativeWork.WorkStatus.APPROVED && status != CreativeWork.WorkStatus.REJECTED) {
            throw new IllegalArgumentException("无效的审核状态");
        }

        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        if (creativeWork.isPresent()) {
            CreativeWork work = creativeWork.get();
            work.setStatus(status);
            work.setUpdatedAt(LocalDateTime.now());
            return creativeWorkRepository.save(work);
        }
        return null;
    }

    /**
     * 获取已审核通过的作品列表（分页）
     */
    public Page<CreativeWork> getApprovedCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.APPROVED, pageable);
    }

    /**
     * 获取已拒绝的作品列表（分页）
     */
    public Page<CreativeWork> getRejectedCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.REJECTED, pageable);
    }

    /**
     * 增加作品点赞数
     */
    public void incrementLikeCount(Long id) {
        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        if (creativeWork.isPresent()) {
            CreativeWork work = creativeWork.get();
            work.setLikeCount(work.getLikeCount() + 1);
            creativeWorkRepository.save(work);
        }
    }

    /**
     * 减少作品点赞数
     */
    public void decrementLikeCount(Long id) {
        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        if (creativeWork.isPresent()) {
            CreativeWork work = creativeWork.get();
            if (work.getLikeCount() > 0) {
                work.setLikeCount(work.getLikeCount() - 1);
                creativeWorkRepository.save(work);
            }
        }
    }
}
