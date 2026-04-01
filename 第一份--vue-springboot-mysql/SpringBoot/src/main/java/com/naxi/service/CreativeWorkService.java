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

    public CreativeWork saveWork(CreativeWork work) {
        if (work.getUserId() != null) {
            User user = userRepository.findById(work.getUserId())
                    .orElseThrow(() -> new BusinessException("用户不存在，无法关联作品"));
            work.setUser(user);
        }

        work.setStatus(CreativeWork.WorkStatus.PENDING);
        work.setCreatedAt(LocalDateTime.now());
        work.setUpdatedAt(LocalDateTime.now());

        return creativeWorkRepository.save(work);
    }

    public CreativeWork uploadCreativeWork(CreativeWork creativeWork) {
        Optional<User> user = userRepository.findById(creativeWork.getUserId());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        creativeWork.setStatus(CreativeWork.WorkStatus.PENDING);
        creativeWork.setLikeCount(0);
        creativeWork.setCreatedAt(LocalDateTime.now());
        creativeWork.setUpdatedAt(LocalDateTime.now());
        creativeWork.setUser(user.get());

        return creativeWorkRepository.save(creativeWork);
    }

    public Page<CreativeWork> getCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.APPROVED, pageable);
    }

    public Page<CreativeWork> getCreativeWorks(CreativeWork.WorkStatus status, Pageable pageable) {
        return creativeWorkRepository.findByStatus(status, pageable);
    }

    public Page<CreativeWork> getAllCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findAll(pageable);
    }

    public Page<CreativeWork> getUserCreativeWorks(Long userId, Pageable pageable) {
        return creativeWorkRepository.findByUserId(userId, pageable);
    }

    public Page<CreativeWork> getUserCreativeWorks(Long userId, CreativeWork.WorkStatus status, Pageable pageable) {
        return creativeWorkRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    public CreativeWork getCreativeWorkById(Long id) {
        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        return creativeWork.orElse(null);
    }

    public void deleteCreativeWork(Long id) {
        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        if (creativeWork.isPresent()) {
            creativeWorkRepository.deleteById(id);
        }
    }

    public Page<CreativeWork> getPendingCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.PENDING, pageable);
    }

    public CreativeWork reviewCreativeWork(Long id, CreativeWork.WorkStatus status) {
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

    public Page<CreativeWork> getApprovedCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.APPROVED, pageable);
    }

    public Page<CreativeWork> getRejectedCreativeWorks(Pageable pageable) {
        return creativeWorkRepository.findByStatus(CreativeWork.WorkStatus.REJECTED, pageable);
    }

    public void incrementLikeCount(Long id) {
        Optional<CreativeWork> creativeWork = creativeWorkRepository.findById(id);
        if (creativeWork.isPresent()) {
            CreativeWork work = creativeWork.get();
            work.setLikeCount(work.getLikeCount() + 1);
            creativeWorkRepository.save(work);
        }
    }

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
