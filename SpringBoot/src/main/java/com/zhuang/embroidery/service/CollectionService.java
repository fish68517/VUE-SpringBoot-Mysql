package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.CollectionListResponse;
import com.zhuang.embroidery.dto.CollectionResponse;
import com.zhuang.embroidery.entity.Artwork;
import com.zhuang.embroidery.entity.Collection;
import com.zhuang.embroidery.repository.ArtworkRepository;
import com.zhuang.embroidery.repository.CollectionRepository;
import com.zhuang.embroidery.repository.UserRepository;
import com.zhuang.embroidery.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;

    /**
     * 收藏作品
     *
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @return 收藏响应
     * @throws IllegalArgumentException 当用户或作品不存在，或已收藏时抛出
     */
    @Transactional
    public CollectionResponse collectArtwork(Long userId, Long artworkId) {
        log.info("收藏作品: userId={}, artworkId={}", userId, artworkId);

        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            log.warn("用户不存在: userId={}", userId);
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证作品是否存在
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        // 检查是否已收藏
        if (collectionRepository.existsByUserIdAndArtworkId(userId, artworkId)) {
            log.warn("作品已收藏: userId={}, artworkId={}", userId, artworkId);
            throw new IllegalArgumentException("作品已收藏");
        }

        // 创建收藏记录
        Collection collection = Collection.builder()
                .userId(userId)
                .artworkId(artworkId)
                .build();

        collection = collectionRepository.save(collection);

        // 更新作品的收藏计数
        artwork.setCollectCount(artwork.getCollectCount() + 1);
        artworkRepository.save(artwork);

        log.info("作品收藏成功: collectionId={}, userId={}, artworkId={}", collection.getId(), userId, artworkId);

        return CollectionResponse.fromCollection(collection);
    }

    /**
     * 取消收藏作品
     *
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @throws IllegalArgumentException 当用户或作品不存在，或未收藏时抛出
     */
    @Transactional
    public void uncollectArtwork(Long userId, Long artworkId) {
        log.info("取消收藏作品: userId={}, artworkId={}", userId, artworkId);

        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            log.warn("用户不存在: userId={}", userId);
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证作品是否存在
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        // 查询收藏记录
        Collection collection = collectionRepository.findByUserIdAndArtworkId(userId, artworkId)
                .orElseThrow(() -> {
                    log.warn("收藏记录不存在: userId={}, artworkId={}", userId, artworkId);
                    return new IllegalArgumentException("收藏记录不存在");
                });

        // 删除收藏记录
        collectionRepository.delete(collection);

        // 更新作品的收藏计数
        if (artwork.getCollectCount() > 0) {
            artwork.setCollectCount(artwork.getCollectCount() - 1);
            artworkRepository.save(artwork);
        }

        log.info("取消收藏成功: userId={}, artworkId={}", userId, artworkId);
    }

    /**
     * 获取用户收藏列表（分页）
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 收藏列表响应
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    public CollectionListResponse getUserCollections(Long userId, Integer pageNum, Integer pageSize) {
        log.info("获取用户收藏列表: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);

        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            log.warn("用户不存在: userId={}", userId);
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询用户收藏
        Page<Collection> page = collectionRepository.findByUserId(userId, pageable);

        // 构建响应
        List<CollectionResponse> items = page.getContent()
                .stream()
                .map(CollectionResponse::fromCollection)
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return CollectionListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 检查用户是否收藏了某个作品
     *
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @return 是否收藏
     */
    public boolean isArtworkCollected(Long userId, Long artworkId) {
        log.debug("检查作品是否被收藏: userId={}, artworkId={}", userId, artworkId);
        return collectionRepository.existsByUserIdAndArtworkId(userId, artworkId);
    }

    /**
     * 获取用户的收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    public long getUserCollectionCount(Long userId) {
        log.debug("获取用户收藏数量: userId={}", userId);
        return collectionRepository.countByUserId(userId);
    }

    /**
     * 获取作品被收藏的次数
     *
     * @param artworkId 作品ID
     * @return 收藏次数
     */
    public long getArtworkCollectionCount(Long artworkId) {
        log.debug("获取作品收藏次数: artworkId={}", artworkId);
        return collectionRepository.countByArtworkId(artworkId);
    }

}
