package com.naxi.service;

import com.naxi.entity.Pattern;
import com.naxi.repository.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PatternService {
    @Autowired
    private PatternRepository patternRepository;

    /**
     * 根据ID获取纹样
     */
    public Pattern getPatternById(Long id) {
        Optional<Pattern> pattern = patternRepository.findById(id);
        if (pattern.isPresent()) {
            // 增加浏览量
            Pattern p = pattern.get();
            p.setViewCount(p.getViewCount() + 1);
            patternRepository.save(p);
            return p;
        }
        return null;
    }

    /**
     * 获取所有纹样（分页）
     */
    public Page<Pattern> getPatterns(Pageable pageable) {
        return patternRepository.findAll(pageable);
    }

    /**
     * 按分类获取纹样
     */
    public Page<Pattern> getPatternsByCategory(Pattern.PatternCategory category, Pageable pageable) {
        return patternRepository.findByCategory(category, pageable);
    }

    /**
     * 搜索纹样（按关键词）
     */
    public Page<Pattern> searchPatterns(String keyword, Pageable pageable) {
        return patternRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 按分类和关键词搜索纹样
     */
    public Page<Pattern> searchPatternsByCategoryAndKeyword(Pattern.PatternCategory category, String keyword, Pageable pageable) {
        return patternRepository.searchByCategoryAndKeyword(category, keyword, pageable);
    }

    /**
     * 按应用场景搜索纹样
     */
    public Page<Pattern> searchPatternsByApplicationScenario(String scenario, Pageable pageable) {
        return patternRepository.searchByApplicationScenario(scenario, pageable);
    }

    /**
     * 创建纹样
     */
    public Pattern savePattern(Pattern pattern) {
        pattern.setCreatedAt(LocalDateTime.now());
        pattern.setUpdatedAt(LocalDateTime.now());
        return patternRepository.save(pattern);
    }

    /**
     * 更新纹样
     */
    public Pattern updatePattern(Long id, Pattern patternDetails) {
        Optional<Pattern> pattern = patternRepository.findById(id);
        if (pattern.isPresent()) {
            Pattern p = pattern.get();
            if (patternDetails.getName() != null) {
                p.setName(patternDetails.getName());
            }
            if (patternDetails.getCategory() != null) {
                p.setCategory(patternDetails.getCategory());
            }
            if (patternDetails.getDescription() != null) {
                p.setDescription(patternDetails.getDescription());
            }
            if (patternDetails.getCulturalBackground() != null) {
                p.setCulturalBackground(patternDetails.getCulturalBackground());
            }
            if (patternDetails.getImageUrl() != null) {
                p.setImageUrl(patternDetails.getImageUrl());
            }
            if (patternDetails.getDownloadUrl() != null) {
                p.setDownloadUrl(patternDetails.getDownloadUrl());
            }
            if (patternDetails.getApplicationScenarios() != null) {
                p.setApplicationScenarios(patternDetails.getApplicationScenarios());
            }
            p.setUpdatedAt(LocalDateTime.now());
            return patternRepository.save(p);
        }
        return null;
    }

    /**
     * 删除纹样
     */
    public void deletePattern(Long id) {
        patternRepository.deleteById(id);
    }

    /**
     * 增加下载量
     */
    public void incrementDownloadCount(Long id) {
        Optional<Pattern> pattern = patternRepository.findById(id);
        if (pattern.isPresent()) {
            Pattern p = pattern.get();
            p.setDownloadCount(p.getDownloadCount() + 1);
            patternRepository.save(p);
        }
    }

    /**
     * 增加收藏量
     */
    public void incrementCollectionCount(Long id) {
        Optional<Pattern> pattern = patternRepository.findById(id);
        if (pattern.isPresent()) {
            Pattern p = pattern.get();
            p.setCollectionCount(p.getCollectionCount() + 1);
            patternRepository.save(p);
        }
    }

    /**
     * 减少收藏量
     */
    public void decrementCollectionCount(Long id) {
        Optional<Pattern> pattern = patternRepository.findById(id);
        if (pattern.isPresent()) {
            Pattern p = pattern.get();
            if (p.getCollectionCount() > 0) {
                p.setCollectionCount(p.getCollectionCount() - 1);
                patternRepository.save(p);
            }
        }
    }
}
