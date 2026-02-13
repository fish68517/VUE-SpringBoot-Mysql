package com.naxi.repository;

import com.naxi.entity.CreativeWork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreativeWorkRepository extends JpaRepository<CreativeWork, Long> {
    /**
     * 按用户ID查询原创作品
     */
    Page<CreativeWork> findByUserId(Long userId, Pageable pageable);

    /**
     * 按状态查询原创作品
     */
    Page<CreativeWork> findByStatus(CreativeWork.WorkStatus status, Pageable pageable);

    /**
     * 按用户ID和状态查询原创作品
     */
    Page<CreativeWork> findByUserIdAndStatus(Long userId, CreativeWork.WorkStatus status, Pageable pageable);

    /**
     * 按状态统计原创作品数量
     */
    long countByStatus(CreativeWork.WorkStatus status);
}
