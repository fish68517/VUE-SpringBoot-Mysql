package com.agricultural.service;

import com.agricultural.engine.RecommendationEngine;
import com.agricultural.entity.Recommendation;
import com.agricultural.entity.Warning;
import com.agricultural.repository.RecommendationRepository;
import com.agricultural.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 推荐业务逻辑层
 * 处理推荐相关的业务逻辑，包括生成推荐、获取推荐列表、更新推荐状态等操作
 */
@Service
@Transactional
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private RecommendationEngine recommendationEngine;

    @Autowired
    private WarningService warningService;

    /**
     * 生成推荐
     * 根据预警信息为用户生成农资推荐
     *
     * @param warningId 预警ID
     * @param userId 用户ID
     * @return 生成的推荐列表
     * @throws IllegalArgumentException 当预警不存在或参数无效时抛出异常
     */
    public List<Recommendation> generateRecommendations(Long warningId, Long userId) {
        LoggerUtil.info("开始生成推荐，预警ID: {}, 用户ID: {}", warningId, userId);

        // 验证参数
        if (warningId == null || warningId <= 0) {
            LoggerUtil.warn("生成推荐失败: 预警ID无效");
            throw new IllegalArgumentException("预警ID无效");
        }

        if (userId == null || userId <= 0) {
            LoggerUtil.warn("生成推荐失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        // 获取预警信息
        Warning warning = warningService.getWarningById(warningId);

        // 使用推荐引擎生成推荐
        List<Recommendation> recommendations = recommendationEngine.generateRecommendations(warning, userId);

        // 保存推荐到数据库
        List<Recommendation> savedRecommendations = recommendationRepository.saveAll(recommendations);

        LoggerUtil.info("推荐生成完成，预警ID: {}, 用户ID: {}, 推荐数: {}", 
                warningId, userId, savedRecommendations.size());

        return savedRecommendations;
    }

    /**
     * 获取推荐列表
     * 获取所有推荐信息
     *
     * @return 推荐列表
     */
    public List<Recommendation> getRecommendationList() {
        LoggerUtil.info("获取推荐列表");

        List<Recommendation> recommendations = recommendationRepository.findAll();
        LoggerUtil.info("获取推荐列表成功，推荐总数: {}", recommendations.size());

        return recommendations;
    }

    /**
     * 根据推荐ID获取推荐详情
     * 获取指定ID的推荐信息
     *
     * @param recommendationId 推荐ID
     * @return 推荐对象
     * @throws IllegalArgumentException 当推荐不存在时抛出异常
     */
    public Recommendation getRecommendationById(Long recommendationId) {
        LoggerUtil.info("根据ID获取推荐详情，推荐ID: {}", recommendationId);

        // 验证ID
        if (recommendationId == null || recommendationId <= 0) {
            LoggerUtil.warn("获取推荐失败: 推荐ID无效");
            throw new IllegalArgumentException("推荐ID无效");
        }

        Optional<Recommendation> recommendationOptional = recommendationRepository.findById(recommendationId);
        if (!recommendationOptional.isPresent()) {
            LoggerUtil.warn("获取推荐失败: 推荐不存在，推荐ID: {}", recommendationId);
            throw new IllegalArgumentException("推荐不存在");
        }

        LoggerUtil.info("根据ID获取推荐详情成功，推荐ID: {}", recommendationId);

        return recommendationOptional.get();
    }

    /**
     * 根据预警ID获取推荐列表
     * 获取指定预警的所有推荐信息
     *
     * @param warningId 预警ID
     * @return 推荐列表
     * @throws IllegalArgumentException 当预警ID无效时抛出异常
     */
    public List<Recommendation> getRecommendationsByWarningId(Long warningId) {
        LoggerUtil.info("根据预警ID获取推荐列表，预警ID: {}", warningId);

        // 验证预警ID
        if (warningId == null || warningId <= 0) {
            LoggerUtil.warn("获取推荐列表失败: 预警ID无效");
            throw new IllegalArgumentException("预警ID无效");
        }

        List<Recommendation> recommendations = recommendationRepository.findByWarningIdOrderByPriority(warningId);
        LoggerUtil.info("根据预警ID获取推荐列表成功，预警ID: {}, 推荐数: {}", warningId, recommendations.size());

        return recommendations;
    }

    /**
     * 根据用户ID获取推荐列表
     * 获取指定用户的所有推荐信息
     *
     * @param userId 用户ID
     * @return 推荐列表
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public List<Recommendation> getRecommendationsByUserId(Long userId) {
        LoggerUtil.info("根据用户ID获取推荐列表，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取推荐列表失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);
        LoggerUtil.info("根据用户ID获取推荐列表成功，用户ID: {}, 推荐数: {}", userId, recommendations.size());

        return recommendations;
    }

    /**
     * 根据产品ID获取推荐列表
     * 获取指定产品的所有推荐信息
     *
     * @param productId 产品ID
     * @return 推荐列表
     * @throws IllegalArgumentException 当产品ID无效时抛出异常
     */
    public List<Recommendation> getRecommendationsByProductId(Long productId) {
        LoggerUtil.info("根据产品ID获取推荐列表，产品ID: {}", productId);

        // 验证产品ID
        if (productId == null || productId <= 0) {
            LoggerUtil.warn("获取推荐列表失败: 产品ID无效");
            throw new IllegalArgumentException("产品ID无效");
        }

        List<Recommendation> recommendations = recommendationRepository.findByProductId(productId);
        LoggerUtil.info("根据产品ID获取推荐列表成功，产品ID: {}, 推荐数: {}", productId, recommendations.size());

        return recommendations;
    }

    /**
     * 根据推荐状态获取推荐列表
     * 获取指定状态的所有推荐信息
     *
     * @param status 推荐状态
     * @return 推荐列表
     * @throws IllegalArgumentException 当推荐状态为空时抛出异常
     */
    public List<Recommendation> getRecommendationsByStatus(Recommendation.RecommendationStatus status) {
        LoggerUtil.info("根据推荐状态获取推荐列表，推荐状态: {}", status);

        // 验证推荐状态
        if (status == null) {
            LoggerUtil.warn("获取推荐列表失败: 推荐状态为空");
            throw new IllegalArgumentException("推荐状态不能为空");
        }

        List<Recommendation> recommendations = recommendationRepository.findByStatus(status);
        LoggerUtil.info("根据推荐状态获取推荐列表成功，推荐状态: {}, 推荐数: {}", status, recommendations.size());

        return recommendations;
    }

    /**
     * 获取待处理的推荐列表
     * 获取所有状态为待处理的推荐信息，按优先级排序
     *
     * @return 待处理推荐列表
     */
    public List<Recommendation> getPendingRecommendations() {
        LoggerUtil.info("获取待处理的推荐列表");

        List<Recommendation> recommendations = recommendationRepository.findPendingRecommendations();
        LoggerUtil.info("获取待处理的推荐列表成功，推荐数: {}", recommendations.size());

        return recommendations;
    }

    /**
     * 根据预警ID和用户ID获取推荐列表
     * 获取指定预警和用户的所有推荐信息
     *
     * @param warningId 预警ID
     * @param userId 用户ID
     * @return 推荐列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<Recommendation> getRecommendationsByWarningAndUser(Long warningId, Long userId) {
        LoggerUtil.info("根据预警ID和用户ID获取推荐列表，预警ID: {}, 用户ID: {}", warningId, userId);

        // 验证参数
        if (warningId == null || warningId <= 0) {
            LoggerUtil.warn("获取推荐列表失败: 预警ID无效");
            throw new IllegalArgumentException("预警ID无效");
        }

        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取推荐列表失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        List<Recommendation> recommendations = recommendationRepository.findByWarningIdAndUserId(warningId, userId);
        LoggerUtil.info("根据预警ID和用户ID获取推荐列表成功，预警ID: {}, 用户ID: {}, 推荐数: {}", 
                warningId, userId, recommendations.size());

        return recommendations;
    }

    /**
     * 接受推荐
     * 用户接受推荐，将推荐状态更新为已接受
     *
     * @param recommendationId 推荐ID
     * @return 更新后的推荐对象
     * @throws IllegalArgumentException 当推荐不存在时抛出异常
     */
    public Recommendation acceptRecommendation(Long recommendationId) {
        LoggerUtil.info("接受推荐，推荐ID: {}", recommendationId);

        // 获取推荐
        Recommendation recommendation = getRecommendationById(recommendationId);

        // 检查推荐状态
        if (recommendation.getStatus() != Recommendation.RecommendationStatus.PENDING) {
            LoggerUtil.warn("接受推荐失败: 推荐状态不是待处理，推荐ID: {}, 当前状态: {}", 
                    recommendationId, recommendation.getStatus());
            throw new IllegalArgumentException("只能接受待处理的推荐");
        }

        // 更新状态为已接受
        recommendation.setStatus(Recommendation.RecommendationStatus.ACCEPTED);
        Recommendation updatedRecommendation = recommendationRepository.save(recommendation);

        LoggerUtil.info("推荐接受成功，推荐ID: {}", recommendationId);

        return updatedRecommendation;
    }

    /**
     * 拒绝推荐
     * 用户拒绝推荐，将推荐状态更新为已拒绝
     *
     * @param recommendationId 推荐ID
     * @return 更新后的推荐对象
     * @throws IllegalArgumentException 当推荐不存在时抛出异常
     */
    public Recommendation rejectRecommendation(Long recommendationId) {
        LoggerUtil.info("拒绝推荐，推荐ID: {}", recommendationId);

        // 获取推荐
        Recommendation recommendation = getRecommendationById(recommendationId);

        // 检查推荐状态
        if (recommendation.getStatus() != Recommendation.RecommendationStatus.PENDING) {
            LoggerUtil.warn("拒绝推荐失败: 推荐状态不是待处理，推荐ID: {}, 当前状态: {}", 
                    recommendationId, recommendation.getStatus());
            throw new IllegalArgumentException("只能拒绝待处理的推荐");
        }

        // 更新状态为已拒绝
        recommendation.setStatus(Recommendation.RecommendationStatus.REJECTED);
        Recommendation updatedRecommendation = recommendationRepository.save(recommendation);

        LoggerUtil.info("推荐拒绝成功，推荐ID: {}", recommendationId);

        return updatedRecommendation;
    }

    /**
     * 删除推荐
     * 从数据库中删除指定的推荐记录
     *
     * @param recommendationId 推荐ID
     * @throws IllegalArgumentException 当推荐不存在时抛出异常
     */
    public void deleteRecommendation(Long recommendationId) {
        LoggerUtil.info("删除推荐，推荐ID: {}", recommendationId);

        // 验证推荐是否存在
        Recommendation recommendation = getRecommendationById(recommendationId);

        recommendationRepository.deleteById(recommendationId);
        LoggerUtil.info("推荐删除成功，推荐ID: {}", recommendationId);
    }

    /**
     * 获取推荐统计信息
     * 统计不同状态的推荐数量
     *
     * @return 包含推荐统计信息的字符串
     */
    public String getRecommendationStatistics() {
        LoggerUtil.info("获取推荐统计信息");

        List<Recommendation> allRecommendations = recommendationRepository.findAll();

        long pendingCount = allRecommendations.stream()
                .filter(r -> r.getStatus() == Recommendation.RecommendationStatus.PENDING)
                .count();

        long acceptedCount = allRecommendations.stream()
                .filter(r -> r.getStatus() == Recommendation.RecommendationStatus.ACCEPTED)
                .count();

        long rejectedCount = allRecommendations.stream()
                .filter(r -> r.getStatus() == Recommendation.RecommendationStatus.REJECTED)
                .count();

        String statistics = String.format(
                "推荐总数: %d, 待处理: %d, 已接受: %d, 已拒绝: %d",
                allRecommendations.size(), pendingCount, acceptedCount, rejectedCount
        );

        LoggerUtil.info("推荐统计信息: {}", statistics);

        return statistics;
    }

    /**
     * 检查推荐是否存在
     * 用于验证推荐是否存在
     *
     * @param recommendationId 推荐ID
     * @return 如果推荐存在返回true，否则返回false
     */
    public boolean recommendationExists(Long recommendationId) {
        LoggerUtil.info("检查推荐是否存在，推荐ID: {}", recommendationId);

        if (recommendationId == null || recommendationId <= 0) {
            return false;
        }

        return recommendationRepository.existsById(recommendationId);
    }

    /**
     * 获取推荐总数
     * 获取系统中所有推荐的总数
     *
     * @return 推荐总数
     */
    public long getRecommendationCount() {
        LoggerUtil.info("获取推荐总数");

        long count = recommendationRepository.count();
        LoggerUtil.info("获取推荐总数成功，推荐总数: {}", count);

        return count;
    }

    /**
     * 获取用户的推荐总数
     * 获取指定用户的推荐数量
     *
     * @param userId 用户ID
     * @return 推荐总数
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public long getRecommendationCountByUser(Long userId) {
        LoggerUtil.info("获取用户推荐总数，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取推荐总数失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        long count = getRecommendationsByUserId(userId).size();
        LoggerUtil.info("获取用户推荐总数成功，用户ID: {}, 推荐总数: {}", userId, count);

        return count;
    }
}
