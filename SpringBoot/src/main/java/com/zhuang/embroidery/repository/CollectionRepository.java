package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 收藏数据访问层接口
 */
@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    /**
     * 根据用户ID查询收藏列表（分页）
     *
     * @param userId 用户ID
     * @param pageable 分页信息
     * @return 收藏分页列表
     */
    Page<Collection> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和作品ID查询收藏
     *
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @return 收藏对象
     */
    Optional<Collection> findByUserIdAndArtworkId(Long userId, Long artworkId);

    /**
     * 检查用户是否收藏了某个作品
     *
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @return 是否收藏
     */
    boolean existsByUserIdAndArtworkId(Long userId, Long artworkId);

    /**
     * 根据用户ID删除所有收藏
     *
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 根据作品ID删除所有收藏
     *
     * @param artworkId 作品ID
     */
    void deleteByArtworkId(Long artworkId);

    /**
     * 统计用户的收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    long countByUserId(Long userId);

    /**
     * 统计作品被收藏的次数
     *
     * @param artworkId 作品ID
     * @return 收藏次数
     */
    long countByArtworkId(Long artworkId);
}
