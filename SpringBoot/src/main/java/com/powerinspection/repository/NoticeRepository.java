package com.powerinspection.repository;

import com.powerinspection.entity.Notice;
import com.powerinspection.entity.NoticeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByStatusOrderByPinnedDescPublishTimeDesc(NoticeStatus status);
}
