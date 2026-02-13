package com.naxi.service;

import com.naxi.entity.Collection;
import com.naxi.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private PatternService patternService;

    /**
     * 添加收藏
     * 需求: 12.1
     */
    public Collection addCollection(Long userId, Long patternId) {
        // 检查是否已收藏
        if (collectionRepository.existsByUserIdAndPatternId(userId, patternId)) {
            return null; // 已收藏，返回null
        }

        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setPatternId(patternId);
        collection.setCreatedAt(LocalDateTime.now());

        Collection savedCollection = collectionRepository.save(collection);

        // 增加纹样的收藏量
        patternService.incrementCollectionCount(patternId);

        return savedCollection;
    }

    /**
     * 删除收藏
     * 需求: 12.1
     */
    public boolean deleteCollection(Long collectionId) {
        Optional<Collection> collection = collectionRepository.findById(collectionId);
        if (collection.isPresent()) {
            Collection c = collection.get();
            collectionRepository.deleteById(collectionId);

            // 减少纹样的收藏量
            patternService.decrementCollectionCount(c.getPatternId());

            return true;
        }
        return false;
    }

    /**
     * 删除用户对某个纹样的收藏
     * 需求: 12.1
     */
    public boolean deleteCollectionByUserAndPattern(Long userId, Long patternId) {
        List<Collection> collections = collectionRepository.findByUserId(userId);
        for (Collection collection : collections) {
            if (collection.getPatternId().equals(patternId)) {
                collectionRepository.deleteById(collection.getId());
                // 减少纹样的收藏量
                patternService.decrementCollectionCount(patternId);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取用户收藏列表
     * 需求: 12.2
     */
    public List<Collection> getUserCollections(Long userId) {
        return collectionRepository.findByUserId(userId);
    }

    /**
     * 检查纹样是否被收藏
     * 需求: 12.2
     */
    public boolean isPatternCollected(Long userId, Long patternId) {
        return collectionRepository.existsByUserIdAndPatternId(userId, patternId);
    }

    /**
     * 获取收藏详情
     */
    public Collection getCollectionById(Long collectionId) {
        Optional<Collection> collection = collectionRepository.findById(collectionId);
        return collection.orElse(null);
    }
}
