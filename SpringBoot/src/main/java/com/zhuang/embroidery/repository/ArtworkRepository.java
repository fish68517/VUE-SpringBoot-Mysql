package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Artwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作品数据访问层接口
 */
@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    /**
     * 根据分类查询作品列表（分页）
     *
     * @param category 分类
     * @param pageable 分页信息
     * @return 作品分页列表
     */
    Page<Artwork> findByCategory(String category, Pageable pageable);

    /**
     * 根据状态查询作品列表（分页）
     *
     * @param status 状态
     * @param pageable 分页信息
     * @return 作品分页列表
     */
    Page<Artwork> findByStatus(String status, Pageable pageable);

    /**
     * 根据分类和状态查询作品列表（分页）
     *
     * @param category 分类
     * @param status 状态
     * @param pageable 分页信息
     * @return 作品分页列表
     */
    Page<Artwork> findByCategoryAndStatus(String category, String status, Pageable pageable);

    /**
     * 查询所有已批准的作品（分页）
     *
     * @param pageable 分页信息
     * @return 作品分页列表
     */
    Page<Artwork> findByStatus(String status, Pageable pageable);

    /**
     * 根据创作者查询作品列表
     *
     * @param creator 创作者名称
     * @return 作品列表
     */
    List<Artwork> findByCreator(String creator);

    /**
     * 根据标题模糊查询作品列表（分页）
     *
     * @param title 标题关键词
     * @param pageable 分页信息
     * @return 作品分页列表
     */
    @Query("SELECT a FROM Artwork a WHERE a.title LIKE %:title%")
    Page<Artwork> searchByTitle(@Param("title") String title, Pageable pageable);

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    @Query("SELECT DISTINCT a.category FROM Artwork a WHERE a.category IS NOT NULL")
    List<String> findAllCategories();
}
