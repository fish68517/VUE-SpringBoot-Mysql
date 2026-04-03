package com.health.service;

import com.health.entity.HealthData;
import com.health.repository.HealthDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthDataService {

    @Autowired
    private HealthDataRepository healthDataRepository;

    public HealthData saveHealthData(HealthData healthData) {
        validateHealthData(healthData);

        if (healthData.getRecordedAt() == null) {
            healthData.setRecordedAt(LocalDateTime.now());
        }

        return healthDataRepository.save(healthData);
    }

    private void validateHealthData(HealthData healthData) {
        if (healthData.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (healthData.getHeight() != null) {
            validateRange(healthData.getHeight(), "Height", new BigDecimal("50"), new BigDecimal("250"), "cm");
        }

        if (healthData.getWeight() != null) {
            validateRange(healthData.getWeight(), "Weight", new BigDecimal("10"), new BigDecimal("500"), "kg");
        }

        if (healthData.getHeartRate() != null && (healthData.getHeartRate() < 30 || healthData.getHeartRate() > 200)) {
            throw new IllegalArgumentException("Heart rate must be between 30 and 200 bpm");
        }

        if (healthData.getBodyTemperature() != null) {
            validateRange(healthData.getBodyTemperature(), "Body temperature", new BigDecimal("34"), new BigDecimal("42"), "C");
        }

        if (healthData.getBloodOxygen() != null && (healthData.getBloodOxygen() < 70 || healthData.getBloodOxygen() > 100)) {
            throw new IllegalArgumentException("Blood oxygen must be between 70 and 100");
        }

        if (healthData.getBloodSugar() != null) {
            validateRange(healthData.getBloodSugar(), "Blood sugar", new BigDecimal("2"), new BigDecimal("30"), "mmol/L");
        }

        if (healthData.getSleepDuration() != null) {
            validateRange(healthData.getSleepDuration(), "Sleep duration", BigDecimal.ZERO, new BigDecimal("24"), "hours");
        }
    }

    private void validateRange(BigDecimal value, String fieldName, BigDecimal min, BigDecimal max, String unit) {
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max + " " + unit);
        }
    }

    public List<HealthData> getHealthDataByReviewStatus(String status) {
        return healthDataRepository.findByReviewStatusOrderByRecordedAtDesc(status);
    }

    public HealthData reviewHealthData(Long id, String feedback) {
        HealthData healthData = healthDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health data record not found"));

        healthData.setReviewStatus("REVIEWED");
        healthData.setReviewFeedback(feedback);
        healthData.setFeedbackDate(LocalDateTime.now());

        return healthDataRepository.save(healthData);
    }

    public List<HealthData> getUserHealthData(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        return healthDataRepository.findByUserId(userId);
    }

    public List<HealthData> getHealthDataByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        return healthDataRepository.findByUserIdAndTimeRange(userId, startTime, endTime);
    }

    public List<HealthData> getHealthDataByType(Long userId, String dataType) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (dataType == null || dataType.isBlank()) {
            throw new IllegalArgumentException("Data type is required");
        }
        return healthDataRepository.findByUserIdAndDataType(userId, dataType);
    }

    public List<HealthData> getLatestHealthData(Long userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        return healthDataRepository.findLatestByUserId(userId, limit);
    }

    public HealthDataStatistics getHealthDataStatistics(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        List<HealthData> healthDataList = healthDataRepository.findByUserId(userId);
        if (healthDataList.isEmpty()) {
            return new HealthDataStatistics(userId, 0, null, null, null, null, null, null);
        }

        List<BigDecimal> heights = healthDataList.stream()
                .filter(item -> item.getHeight() != null)
                .map(HealthData::getHeight)
                .collect(Collectors.toList());

        List<BigDecimal> weights = healthDataList.stream()
                .filter(item -> item.getWeight() != null)
                .map(HealthData::getWeight)
                .collect(Collectors.toList());

        List<Integer> heartRates = healthDataList.stream()
                .filter(item -> item.getHeartRate() != null)
                .map(HealthData::getHeartRate)
                .collect(Collectors.toList());

        BigDecimal averageHeight = heights.isEmpty() ? null : average(heights);
        BigDecimal averageWeight = weights.isEmpty() ? null : average(weights);
        Double averageHeartRate = heartRates.isEmpty()
                ? null
                : heartRates.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);

        BigDecimal maxWeight = weights.isEmpty() ? null : weights.stream().max(Comparator.naturalOrder()).orElse(null);
        BigDecimal minWeight = weights.isEmpty() ? null : weights.stream().min(Comparator.naturalOrder()).orElse(null);
        LocalDateTime latestRecordTime = healthDataList.stream()
                .map(HealthData::getRecordedAt)
                .filter(recordedAt -> recordedAt != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new HealthDataStatistics(
                userId,
                healthDataList.size(),
                averageHeight,
                averageWeight,
                averageHeartRate,
                maxWeight,
                minWeight,
                latestRecordTime
        );
    }

    private BigDecimal average(List<BigDecimal> values) {
        return values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(values.size()), 2, RoundingMode.HALF_UP);
    }

    public static class HealthDataStatistics {
        private final Long userId;
        private final Integer totalRecords;
        private final BigDecimal averageHeight;
        private final BigDecimal averageWeight;
        private final Double averageHeartRate;
        private final BigDecimal maxWeight;
        private final BigDecimal minWeight;
        private final LocalDateTime latestRecordTime;

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
