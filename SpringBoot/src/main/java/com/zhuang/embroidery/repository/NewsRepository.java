package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 资讯数据访问层
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
