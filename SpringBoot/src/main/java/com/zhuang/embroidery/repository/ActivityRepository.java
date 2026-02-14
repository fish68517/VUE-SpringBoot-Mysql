package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 活动数据访问层
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
