package com.health.service;

import com.health.entity.HealthData;
import com.health.repository.HealthDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康数据服务类
 * 处理健康数据的保存、查询、统计和趋势分析逻辑
 */
@Service
public class HealthDataService {

    @Autowired
    private HealthDataRepository healthDataRepository;

    /**
     * 保存健康数据
     * 验证数据范围后保存健康数据到数据库
     *
     * @param healthData 健康数据对象
     * @return 保存后的健康数据对象
     * @throws IllegalArgumentException 如果数据超出合理范围
     */
    public HealthData saveHealthData(HealthData healthData) {
        // 验证身高范围（单位：cm，合理范围：50-250cm）
        if (healthData.getHeight() != null) {
            if (healthData.getHeight().compareTo(new BigDecimal("50")) < 0 || 
                healthData.getHeight().compareTo(new BigDecimal("250")) > 0) {
                throw new IllegalArgumentException("身高应在50-250cm之间");
            }
        }

        // 验证体重范围（单位：kg，合理范围：10-500kg）
        if (healthData.getWeight() != null) {
            if (healthData.getWeight().compareTo(new BigDecimal("10")) < 0 || 
                healthData.getWeight().compareTo(new BigDecimal("500")) > 0) {
                throw new IllegalArgumentException("体重应在10-500kg之间");
            }
        }

        // 验证心率范围（单位：次/分钟，合理范围：30-200）
        if (healthData.getHeartRate() != null) {
            if (healthData.getHeartRate() < 30 || healthData.getHeartRate() > 200) {
                throw new IllegalArgumentException("心率应在30-200次/分钟之间");
            }
        }

        // 验证用户ID不为空
        if (healthData.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 设置记录时间（如果未设置）
        if (healthData.getRecordedAt() == null) {
            healthData.setRecordedAt(LocalDateTime.now());
        }

        // 保存健康数据
        return healthDataRepository.save(healthData);
    }

    /**
     * 获取用户的所有健康数据
     * 根据用户ID获取该用户的所有健康数据
     *
     * @param userId 用户ID
     * @return 健康数据列表
     * @throws IllegalArgumentException 如果用户ID为空
     */
    public List<HealthData> getUserHealthData(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        return healthDataRepository.findByUserId(userId);
    }

    /**
     * 按时间范围查询用户的健康数据
     * 根据用户ID和时间范围查询健康数据
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的健康数据列表
     * @throws IllegalArgumentException 如果参数无效
     */
    public List<HealthData> getHealthDataByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }

        return healthDataRepository.findByUserIdAndTimeRange(userId, startTime, endTime);
    }

    /**
     * 获取用户特定类型的健康数据
     * 根据用户ID和数据类型查询健康数据
     *
     * @param userId 用户ID
     * @param dataType 数据类型（ROUTINE或GENDER_SPECIFIC）
     * @return 指定类型的健康数据列表
     * @throws IllegalArgumentException 如果参数无效
     */
    public List<HealthData> getHealthDataByType(Long userId, String dataType) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (dataType == null || dataType.isEmpty()) {
            throw new IllegalArgumentException("数据类型不能为空");
        }

        return healthDataRepository.findByUserIdAndDataType(userId, dataType);
    }

    /**
     * 获取用户最新的健康数据
     * 获取用户最近N条健康数据记录
     *
     * @param userId 用户ID
     * @param limit 获取的记录数
     * @return 最新的健康数据列表
     * @throws IllegalArgumentException 如果参数无效
     */
    public List<HealthData> getLatestHealthData(Long userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("记录数应大于0");
        }

        return healthDataRepository.findLatestByUserId(userId, limit);
    }

    /**
     * 健康数据统计
     * 对用户的健康数据进行统计分析，用于趋势分析
     *
     * @param userId 用户ID
     * @return 包含统计信息的HealthDataStatistics对象
     * @throws IllegalArgumentException 如果用户ID为空
     */
    public HealthDataStatistics getHealthDataStatistics(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        List<HealthData> healthDataList = healthDataRepository.findByUserId(userId);

        if (healthDataList.isEmpty()) {
            return new HealthDataStatistics(userId, 0, null, null, null, null, null, null);
        }

        // 统计身高
        List<BigDecimal> heights = healthDataList.stream()
                .filter(h -> h.getHeight() != null)
                .map(HealthData::getHeight)
                .collect(Collectors.toList());

        // 统计体重
        List<BigDecimal> weights = healthDataList.stream()
                .filter(h -> h.getWeight() != null)
                .map(HealthData::getWeight)
                .collect(Collectors.toList());

        // 统计心率
        List<Integer> heartRates = healthDataList.stream()
                .filter(h -> h.getHeartRate() != null)
                .map(HealthData::getHeartRate)
                .collect(Collectors.toList());

        BigDecimal avgHeight = heights.isEmpty() ? null : 
                heights.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(heights.size()), 2, java.math.RoundingMode.HALF_UP);

        BigDecimal avgWeight = weights.isEmpty() ? null : 
                weights.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(weights.size()), 2, java.math.RoundingMode.HALF_UP);

        Double avgHeartRate = heartRates.isEmpty() ? null : 
                heartRates.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);

        BigDecimal maxWeight = weights.isEmpty() ? null : weights.stream().max(BigDecimal::compareTo).orElse(null);
        BigDecimal minWeight = weights.isEmpty() ? null : weights.stream().min(BigDecimal::compareTo).orElse(null);

        return new HealthDataStatistics(
                userId,
                healthDataList.size(),
                avgHeight,
                avgWeight,
                avgHeartRate,
                maxWeight,
                minWeight,
                healthDataList.get(0).getRecordedAt()
        );
    }

    /**
     * 健康数据统计结果类
     * 用于返回健康数据的统计分析结果
     */
    public static class HealthDataStatistics {
        private Long userId;
        private Integer totalRecords;
        private BigDecimal averageHeight;
        private BigDecimal averageWeight;
        private Double averageHeartRate;
        private BigDecimal maxWeight;
        private BigDecimal minWeight;
        private LocalDateTime latestRecordTime;

        public HealthDataStatistics(Long userId, Integer totalRecords, BigDecimal averageHeight,
                                   BigDecimal averageWeight, Double averageHeartRate,
                                   BigDecimal maxWeight, BigDecimal minWeight,
                                   LocalDateTime latestRecordTime) {
            this.userId = userId;
            this.totalRecords = totalRecords;
            this.averageHeight = averageHeight;
            this.averageWeight = averageWeight;
            this.averageHeartRate = averageHeartRate;
            this.maxWeight = maxWeight;
            this.minWeight = minWeight;
            this.latestRecordTime = latestRecordTime;
        }

        // Getters
        public Long getUserId() {
            return userId;
        }

        public Integer getTotalRecords() {
            return totalRecords;
        }

        public BigDecimal getAverageHeight() {
            return averageHeight;
        }

        public BigDecimal getAverageWeight() {
            return averageWeight;
        }

        public Double getAverageHeartRate() {
            return averageHeartRate;
        }

        public BigDecimal getMaxWeight() {
            return maxWeight;
        }

        public BigDecimal getMinWeight() {
            return minWeight;
        }

        public LocalDateTime getLatestRecordTime() {
            return latestRecordTime;
        }
    }
}
