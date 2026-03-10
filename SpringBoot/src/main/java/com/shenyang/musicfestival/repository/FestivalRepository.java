package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Festival entity
 */
@Repository
public interface FestivalRepository extends JpaRepository<Festival, Long> {

    Optional<Festival> findByStatus(String status);

    // 修复报错：改为查出多条时只取第一条（按开始时间倒序）
    Optional<Festival> findFirstByStatusOrderByStartDateDesc(String status);

}
