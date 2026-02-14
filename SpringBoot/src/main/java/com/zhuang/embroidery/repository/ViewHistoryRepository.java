package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.ViewHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 浏览历史数据访问层接口
 */
@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    /**
     * 根据用户ID查询浏览历史列表（分页）
     *
     * @param userId 用户ID
     * @param pageable 分页信息
     * @return 浏览历史分页列表
     */
    Page<ViewHistory> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和内容类型查询浏览历史列表（分页）
     *
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param pageable 分页信息
     * @return 浏览历史分页列表
     */
    Page<ViewHistory> findByUserIdAndContentType(Long userId, String contentType, Pageable pageable);

    /**
     * 根据用户ID和内容ID查询浏览历史
     *
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 浏览历史列表
     */
    List<ViewHistory> findByUserIdAndContentId(Long userId, Long contentId);

    /**
     * 根据用户ID删除浏览历史
     *
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
}
