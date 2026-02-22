package com.agricultural.engine;

import com.agricultural.entity.AgriculturalProduct;
import com.agricultural.entity.Crop;
import com.agricultural.entity.Recommendation;
import com.agricultural.entity.Warning;
import com.agricultural.repository.AgriculturalProductRepository;
import com.agricultural.repository.CropRepository;
import com.agricultural.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐引擎类
 * 根据预警类型、作物信息和库存状态生成智能农资推荐
 * 实现推荐规则引擎、优先级排序和库存检查逻辑
 */
@Component
public class RecommendationEngine {

    @Autowired
    private AgriculturalProductRepository productRepository;

    @Autowired
    private CropRepository cropRepository;

    /**
     * 推荐规则配置
     * 定义预警类型与农资产品类别的映射关系
     * 以及对应的推荐优先级
     */
    private static final Map<String, RecommendationRule> RECOMMENDATION_RULES = new HashMap<>();

    static {
        // 暴雨预警规则
        RECOMMENDATION_RULES.put("暴雨", new RecommendationRule(
                "暴雨",
                Arrays.asList("排水剂", "防水剂", "杀菌剂"),
                Arrays.asList(1, 2, 3),
                "暴雨可能导致田间积水和病害，需要排水和防病处理"
        ));

        // 冰雹预警规则
        RECOMMENDATION_RULES.put("冰雹", new RecommendationRule(
                "冰雹",
                Arrays.asList("防护剂", "修复剂", "营养剂"),
                Arrays.asList(1, 2, 3),
                "冰雹会造成植物机械伤害，需要防护和修复"
        ));

        // 大风预警规则
        RECOMMENDATION_RULES.put("大风", new RecommendationRule(
                "大风",
                Arrays.asList("防风剂", "加固剂", "营养剂"),
                Arrays.asList(1, 2, 3),
                "大风可能导致植物倒伏和脱水，需要加固和补充营养"
        ));

        // 干旱预警规则
        RECOMMENDATION_RULES.put("干旱", new RecommendationRule(
                "干旱",
                Arrays.asList("保水剂", "营养剂", "抗旱剂"),
                Arrays.asList(1, 2, 3),
                "干旱会导致植物缺水，需要保水和补充营养"
        ));

        // 低温预警规则
        RECOMMENDATION_RULES.put("低温", new RecommendationRule(
                "低温",
                Arrays.asList("防冻剂", "营养剂", "生长调节剂"),
                Arrays.asList(1, 2, 3),
                "低温会影响植物生长，需要防冻和补充营养"
        ));

        // 高温预警规则
        RECOMMENDATION_RULES.put("高温", new RecommendationRule(
                "高温",
                Arrays.asList("降温剂", "营养剂", "抗热剂"),
                Arrays.asList(1, 2, 3),
                "高温会导致植物热害，需要降温和补充营养"
        ));

        // 病虫害预警规则
        RECOMMENDATION_RULES.put("病虫害", new RecommendationRule(
                "病虫害",
                Arrays.asList("杀虫剂", "杀菌剂", "营养剂"),
                Arrays.asList(1, 2, 3),
                "病虫害会严重影响产量，需要及时防治"
        ));
    }

    /**
     * 生成推荐列表
     * 根据预警信息和用户作物信息生成农资推荐
     *
     * @param warning 预警信息
     * @param userId 用户ID
     * @return 推荐列表，按优先级排序
     */
    public List<Recommendation> generateRecommendations(Warning warning, Long userId) {
        LoggerUtil.info("开始生成推荐，预警ID: {}, 预警类型: {}, 用户ID: {}", 
                warning.getId(), warning.getWarningType(), userId);

        List<Recommendation> recommendations = new ArrayList<>();

        try {
            // 获取推荐规则
            RecommendationRule rule = RECOMMENDATION_RULES.get(warning.getWarningType());
            if (rule == null) {
                LoggerUtil.warn("未找到预警类型的推荐规则，预警类型: {}", warning.getWarningType());
                return recommendations;
            }

            // 获取用户在预警地区的作物
            List<Crop> userCrops = cropRepository.findByUserIdAndRegion(userId, warning.getRegion());
            if (userCrops.isEmpty()) {
                LoggerUtil.info("用户在预警地区没有作物，用户ID: {}, 地区: {}", userId, warning.getRegion());
                return recommendations;
            }

            // 根据推荐规则获取推荐产品
            List<AgriculturalProduct> recommendedProducts = getRecommendedProducts(rule, warning.getRegion());

            // 为每个推荐产品创建推荐记录
            for (int i = 0; i < recommendedProducts.size(); i++) {
                AgriculturalProduct product = recommendedProducts.get(i);
                Integer priority = rule.getPriorities().get(i);
                String reason = generateRecommendationReason(rule, product, userCrops);

                Recommendation recommendation = Recommendation.builder()
                        .warningId(warning.getId())
                        .productId(product.getId())
                        .userId(userId)
                        .priority(priority)
                        .reason(reason)
                        .status(Recommendation.RecommendationStatus.PENDING)
                        .build();

                recommendations.add(recommendation);
                LoggerUtil.info("生成推荐记录，预警ID: {}, 产品ID: {}, 用户ID: {}, 优先级: {}", 
                        warning.getId(), product.getId(), userId, priority);
            }

            // 按优先级排序
            recommendations.sort(Comparator.comparingInt(Recommendation::getPriority));

            LoggerUtil.info("推荐生成完成，预警ID: {}, 用户ID: {}, 推荐数: {}", 
                    warning.getId(), userId, recommendations.size());

        } catch (Exception e) {
            LoggerUtil.error("生成推荐时发生错误，预警ID: {}, 用户ID: {}, 错误信息: {}", 
                    warning.getId(), userId, e.getMessage());
        }

        return recommendations;
    }

    /**
     * 获取推荐产品列表
     * 根据推荐规则和地区获取库存充足的推荐产品
     *
     * @param rule 推荐规则
     * @param region 地区
     * @return 推荐产品列表
     */
    private List<AgriculturalProduct> getRecommendedProducts(RecommendationRule rule, String region) {
        LoggerUtil.info("获取推荐产品列表，预警类型: {}, 地区: {}", rule.getWarningType(), region);

        List<AgriculturalProduct> recommendedProducts = new ArrayList<>();

        // 遍历推荐的产品类别
        for (String category : rule.getProductCategories()) {
            // 获取该类别的库存充足的产品
            List<AgriculturalProduct> products = productRepository.findByCategory(category).stream()
                    .filter(p -> p.getStock() > 0)
                    .sorted(Comparator.comparingInt(AgriculturalProduct::getStock).reversed())
                    .limit(1)
                    .collect(Collectors.toList());

            recommendedProducts.addAll(products);
        }

        LoggerUtil.info("获取推荐产品完成，预警类型: {}, 地区: {}, 产品数: {}", 
                rule.getWarningType(), region, recommendedProducts.size());

        return recommendedProducts;
    }

    /**
     * 生成推荐理由
     * 根据推荐规则、产品和用户作物信息生成推荐理由
     *
     * @param rule 推荐规则
     * @param product 推荐产品
     * @param userCrops 用户作物列表
     * @return 推荐理由
     */
    private String generateRecommendationReason(RecommendationRule rule, AgriculturalProduct product, List<Crop> userCrops) {
        StringBuilder reason = new StringBuilder();

        reason.append(rule.getDescription()).append("。");
        reason.append("推荐产品：").append(product.getProductName()).append("。");

        if (!userCrops.isEmpty()) {
            String cropNames = userCrops.stream()
                    .map(Crop::getCropName)
                    .distinct()
                    .collect(Collectors.joining("、"));
            reason.append("您种植的").append(cropNames).append("需要此产品保护。");
        }

        return reason.toString();
    }

    /**
     * 检查产品库存
     * 验证推荐产品的库存是否充足
     *
     * @param productId 产品ID
     * @return 如果库存充足返回true，否则返回false
     */
    public boolean checkProductStock(Long productId) {
        LoggerUtil.info("检查产品库存，产品ID: {}", productId);

        Optional<AgriculturalProduct> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            LoggerUtil.warn("产品不存在，产品ID: {}", productId);
            return false;
        }

        AgriculturalProduct product = productOptional.get();
        boolean hasStock = product.getStock() > 0;

        LoggerUtil.info("产品库存检查完成，产品ID: {}, 库存: {}, 是否充足: {}", 
                productId, product.getStock(), hasStock);

        return hasStock;
    }

    /**
     * 获取推荐规则
     * 返回指定预警类型的推荐规则
     *
     * @param warningType 预警类型
     * @return 推荐规则，如果不存在返回null
     */
    public RecommendationRule getRecommendationRule(String warningType) {
        LoggerUtil.info("获取推荐规则，预警类型: {}", warningType);
        return RECOMMENDATION_RULES.get(warningType);
    }

    /**
     * 获取所有推荐规则
     * 返回所有配置的推荐规则
     *
     * @return 推荐规则映射
     */
    public Map<String, RecommendationRule> getAllRecommendationRules() {
        LoggerUtil.info("获取所有推荐规则");
        return new HashMap<>(RECOMMENDATION_RULES);
    }

    /**
     * 推荐规则内部类
     * 定义推荐规则的结构
     */
    public static class RecommendationRule {
        private String warningType;           // 预警类型
        private List<String> productCategories; // 推荐产品类别列表
        private List<Integer> priorities;      // 优先级列表
        private String description;            // 规则描述

        public RecommendationRule(String warningType, List<String> productCategories, 
                                 List<Integer> priorities, String description) {
            this.warningType = warningType;
            this.productCategories = productCategories;
            this.priorities = priorities;
            this.description = description;
        }

        // Getters
        public String getWarningType() {
            return warningType;
        }

        public List<String> getProductCategories() {
            return productCategories;
        }

        public List<Integer> getPriorities() {
            return priorities;
        }

        public String getDescription() {
            return description;
        }
    }
}
