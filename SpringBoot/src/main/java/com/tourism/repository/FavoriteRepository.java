package com.tourism.repository;

import com.tourism.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 收藏数据访问接口
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户ID查询收藏列表（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 收藏分页结果
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID查询所有收藏
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Favorite> findByUserId(Long userId);
    
    /**
     * 根据用户ID和目标类型查询收藏列表
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 收藏列表
     */
    List<Favorite> findByUserIdAndTargetType(Long userId, String targetType);
    
    /**
     * 根据用户ID、目标类型和目标ID查询收藏
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 收藏对象
     */
    Optional<Favorite> findByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
    
    /**
     * 检查用户是否收藏了指定目标
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 是否存在
     */
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
    
    /**
     * 统计用户的收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户指定类型的收藏数量
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 收藏数量
     */
    long countByUserIdAndTargetType(Long userId, String targetType);
    
    /**
     * 统计指定目标被收藏的次数
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 收藏次数
     */
    long countByTargetTypeAndTargetId(String targetType, Long targetId);
    
    /**
     * 根据目标类型和目标ID查询收藏列表
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 收藏列表
     */
    List<Favorite> findByTargetTypeAndTargetId(String targetType, Long targetId);
}
