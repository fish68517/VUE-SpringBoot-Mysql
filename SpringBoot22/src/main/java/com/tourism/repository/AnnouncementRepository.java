package com.tourism.repository;

import com.tourism.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 公告数据访问接口
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    /**
     * 按创建时间倒序分页查询所有公告
     * @param pageable 分页参数
     * @return 公告分页数据
     */
    Page<Announcement> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
