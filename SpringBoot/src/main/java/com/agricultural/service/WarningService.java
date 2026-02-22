package com.agricultural.service;

import com.agricultural.entity.Warning;
import com.agricultural.repository.WarningRepository;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预警业务逻辑层
 * 处理极端天气预警相关的业务逻辑，包括发布预警、获取预警列表、更新预警状态、处理预警过期等操作
 */
@Service
@Transactional
public class WarningService {

    @Autowired
    private WarningRepository warningRepository;

    /**
     * 发布预警
     * 创建新的极端天气预警信息
     *
     * @param warningType 预警类型（如：暴雨、冰雹、大风等）
     * @param region 影响地区
     * @param severity 预警等级
     * @param description 预警描述
     * @param startTime 预警开始时间
     * @param endTime 预警结束时间
     * @return 创建的预警对象
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public Warning publishWarning(String warningType, String region, Warning.WarningSeverity severity,
                                  String description, LocalDateTime startTime, LocalDateTime endTime) {
        LoggerUtil.info("开始发布预警，预警类型: {}, 地区: {}, 等级: {}", warningType, region, severity);

        // 验证预警类型
        if (StringUtil.isBlank(warningType)) {
            LoggerUtil.warn("发布预警失败: 预警类型为空");
            throw new IllegalArgumentException("预警类型不能为空");
        }

        // 验证地区
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("发布预警失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 验证预警等级
        if (severity == null) {
            LoggerUtil.warn("发布预警失败: 预警等级为空");
            throw new IllegalArgumentException("预警等级不能为空");
        }

        // 验证时间参数
        if (startTime == null || endTime == null) {
            LoggerUtil.warn("发布预警失败: 预警时间为空");
            throw new IllegalArgumentException("预警开始时间和结束时间不能为空");
        }

        // 验证时间范围
        if (startTime.isAfter(endTime)) {
            LoggerUtil.warn("发布预警失败: 开始时间晚于结束时间，开始时间: {}, 结束时间: {}", startTime, endTime);
            throw new IllegalArgumentException("预警开始时间不能晚于结束时间");
        }

        // 验证预警时间不能在过去
        if (endTime.isBefore(LocalDateTime.now())) {
            LoggerUtil.warn("发布预警失败: 预警结束时间已过期，结束时间: {}", endTime);
            throw new IllegalArgumentException("预警结束时间不能在过去");
        }

        // 创建预警对象
        Warning warning = Warning.builder()
                .warningType(warningType)
                .region(region)
                .severity(severity)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .status(Warning.WarningStatus.ACTIVE)
                .build();

        // 保存预警
        Warning savedWarning = warningRepository.save(warning);
        LoggerUtil.info("预警发布成功，预警ID: {}, 预警类型: {}, 地区: {}", savedWarning.getId(), warningType, region);

        return savedWarning;
    }

    /**
     * 获取预警列表
     * 获取所有预警信息
     *
     * @return 预警列表
     */
    public List<Warning> getWarningList() {
        LoggerUtil.info("获取预警列表");

        List<Warning> warnings = warningRepository.findAll();
        LoggerUtil.info("获取预警列表成功，预警总数: {}", warnings.size());

        return warnings;
    }

    /**
     * 根据地区获取预警列表
     * 获取指定地区的所有预警信息
     *
     * @param region 地区
     * @return 预警列表
     * @throws IllegalArgumentException 当地区为空时抛出异常
     */
    public List<Warning> getWarningsByRegion(String region) {
        LoggerUtil.info("根据地区获取预警列表，地区: {}", region);

        // 验证地区参数
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取预警列表失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        List<Warning> warnings = warningRepository.findByRegion(region);
        LoggerUtil.info("根据地区获取预警列表成功，地区: {}, 预警数: {}", region, warnings.size());

        return warnings;
    }

    /**
     * 根据预警类型获取预警列表
     * 获取指定类型的所有预警信息
     *
     * @param warningType 预警类型
     * @return 预警列表
     * @throws IllegalArgumentException 当预警类型为空时抛出异常
     */
    public List<Warning> getWarningsByType(String warningType) {
        LoggerUtil.info("根据预警类型获取预警列表，预警类型: {}", warningType);

        // 验证预警类型
        if (StringUtil.isBlank(warningType)) {
            LoggerUtil.warn("获取预警列表失败: 预警类型为空");
            throw new IllegalArgumentException("预警类型不能为空");
        }

        List<Warning> warnings = warningRepository.findByWarningType(warningType);
        LoggerUtil.info("根据预警类型获取预警列表成功，预警类型: {}, 预警数: {}", warningType, warnings.size());

        return warnings;
    }

    /**
     * 根据预警状态获取预警列表
     * 获取指定状态的所有预警信息
     *
     * @param status 预警状态
     * @return 预警列表
     * @throws IllegalArgumentException 当预警状态为空时抛出异常
     */
    public List<Warning> getWarningsByStatus(Warning.WarningStatus status) {
        LoggerUtil.info("根据预警状态获取预警列表，预警状态: {}", status);

        // 验证预警状态
        if (status == null) {
            LoggerUtil.warn("获取预警列表失败: 预警状态为空");
            throw new IllegalArgumentException("预警状态不能为空");
        }

        List<Warning> warnings = warningRepository.findByStatus(status);
        LoggerUtil.info("根据预警状态获取预警列表成功，预警状态: {}, 预警数: {}", status, warnings.size());

        return warnings;
    }

    /**
     * 根据地区和预警类型获取预警列表
     * 获取指定地区和类型的所有预警信息
     *
     * @param region 地区
     * @param warningType 预警类型
     * @return 预警列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<Warning> getWarningsByRegionAndType(String region, String warningType) {
        LoggerUtil.info("根据地区和预警类型获取预警列表，地区: {}, 预警类型: {}", region, warningType);

        // 验证地区
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取预警列表失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 验证预警类型
        if (StringUtil.isBlank(warningType)) {
            LoggerUtil.warn("获取预警列表失败: 预警类型为空");
            throw new IllegalArgumentException("预警类型不能为空");
        }

        List<Warning> warnings = warningRepository.findByRegionAndWarningType(region, warningType);
        LoggerUtil.info("根据地区和预警类型获取预警列表成功，地区: {}, 预警类型: {}, 预警数: {}", region, warningType, warnings.size());

        return warnings;
    }

    /**
     * 根据地区和预警状态获取预警列表
     * 获取指定地区和状态的所有预警信息
     *
     * @param region 地区
     * @param status 预警状态
     * @return 预警列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<Warning> getWarningsByRegionAndStatus(String region, Warning.WarningStatus status) {
        LoggerUtil.info("根据地区和预警状态获取预警列表，地区: {}, 预警状态: {}", region, status);

        // 验证地区
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取预警列表失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 验证预警状态
        if (status == null) {
            LoggerUtil.warn("获取预警列表失败: 预警状态为空");
            throw new IllegalArgumentException("预警状态不能为空");
        }

        List<Warning> warnings = warningRepository.findByRegionAndStatus(region, status);
        LoggerUtil.info("根据地区和预警状态获取预警列表成功，地区: {}, 预警状态: {}, 预警数: {}", region, status, warnings.size());

        return warnings;
    }

    /**
     * 根据地区、预警类型和状态获取预警列表
     * 获取指定地区、类型和状态的所有预警信息
     *
     * @param region 地区
     * @param warningType 预警类型
     * @param status 预警状态
     * @return 预警列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<Warning> getWarningsByRegionTypeAndStatus(String region, String warningType, Warning.WarningStatus status) {
        LoggerUtil.info("根据地区、预警类型和状态获取预警列表，地区: {}, 预警类型: {}, 预警状态: {}", region, warningType, status);

        // 验证地区
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取预警列表失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 验证预警类型
        if (StringUtil.isBlank(warningType)) {
            LoggerUtil.warn("获取预警列表失败: 预警类型为空");
            throw new IllegalArgumentException("预警类型不能为空");
        }

        // 验证预警状态
        if (status == null) {
            LoggerUtil.warn("获取预警列表失败: 预警状态为空");
            throw new IllegalArgumentException("预警状态不能为空");
        }

        List<Warning> warnings = warningRepository.findByRegionAndWarningTypeAndStatus(region, warningType, status);
        LoggerUtil.info("根据地区、预警类型和状态获取预警列表成功，地区: {}, 预警类型: {}, 预警状态: {}, 预警数: {}", 
                region, warningType, status, warnings.size());

        return warnings;
    }

    /**
     * 获取活跃的预警列表
     * 获取所有状态为活跃且未过期的预警信息
     *
     * @return 活跃预警列表
     */
    public List<Warning> getActiveWarnings() {
        LoggerUtil.info("获取活跃的预警列表");

        LocalDateTime currentTime = LocalDateTime.now();
        List<Warning> activeWarnings = warningRepository.findActiveWarnings(currentTime);

        LoggerUtil.info("获取活跃的预警列表成功，活跃预警数: {}", activeWarnings.size());

        return activeWarnings;
    }

    /**
     * 根据ID获取预警详情
     * 获取指定ID的预警信息
     *
     * @param warningId 预警ID
     * @return 预警对象
     * @throws IllegalArgumentException 当预警不存在时抛出异常
     */
    public Warning getWarningById(Long warningId) {
        LoggerUtil.info("根据ID获取预警详情，预警ID: {}", warningId);

        // 验证ID
        if (warningId == null || warningId <= 0) {
            LoggerUtil.warn("获取预警失败: 预警ID无效");
            throw new IllegalArgumentException("预警ID无效");
        }

        Optional<Warning> warningOptional = warningRepository.findById(warningId);
        if (!warningOptional.isPresent()) {
            LoggerUtil.warn("获取预警失败: 预警不存在，预警ID: {}", warningId);
            throw new IllegalArgumentException("预警不存在");
        }

        LoggerUtil.info("根据ID获取预警详情成功，预警ID: {}", warningId);

        return warningOptional.get();
    }

    /**
     * 更新预警状态
     * 更新指定预警的状态（如：从活跃变为过期或取消）
     *
     * @param warningId 预警ID
     * @param status 新状态
     * @return 更新后的预警对象
     * @throws IllegalArgumentException 当预警不存在或状态无效时抛出异常
     */
    public Warning updateWarningStatus(Long warningId, Warning.WarningStatus status) {
        LoggerUtil.info("更新预警状态，预警ID: {}, 新状态: {}", warningId, status);

        // 获取预警
        Warning warning = getWarningById(warningId);

        // 验证新状态
        if (status == null) {
            LoggerUtil.warn("更新预警状态失败: 新状态为空，预警ID: {}", warningId);
            throw new IllegalArgumentException("预警状态不能为空");
        }

        // 更新状态
        warning.setStatus(status);
        Warning updatedWarning = warningRepository.save(warning);

        LoggerUtil.info("预警状态更新成功，预警ID: {}, 新状态: {}", warningId, status);

        return updatedWarning;
    }

    /**
     * 更新预警信息
     * 更新指定预警的详细信息（如：描述、时间范围等）
     *
     * @param warningId 预警ID
     * @param description 预警描述
     * @param endTime 预警结束时间
     * @return 更新后的预警对象
     * @throws IllegalArgumentException 当预警不存在或参数无效时抛出异常
     */
    public Warning updateWarning(Long warningId, String description, LocalDateTime endTime) {
        LoggerUtil.info("更新预警信息，预警ID: {}", warningId);

        // 获取预警
        Warning warning = getWarningById(warningId);

        // 验证并更新描述
        if (StringUtil.isNotBlank(description)) {
            warning.setDescription(description);
        }

        // 验证并更新结束时间
        if (endTime != null) {
            if (endTime.isBefore(warning.getStartTime())) {
                LoggerUtil.warn("更新预警失败: 结束时间早于开始时间，预警ID: {}", warningId);
                throw new IllegalArgumentException("预警结束时间不能早于开始时间");
            }
            warning.setEndTime(endTime);
        }

        Warning updatedWarning = warningRepository.save(warning);
        LoggerUtil.info("预警信息更新成功，预警ID: {}", warningId);

        return updatedWarning;
    }

    /**
     * 处理预警过期
     * 自动将所有已过期的预警状态更新为过期
     * 过期判断标准：当前时间已超过预警结束时间
     *
     * @return 被标记为过期的预警数量
     */
    public int handleExpiredWarnings() {
        LoggerUtil.info("开始处理过期预警");

        LocalDateTime currentTime = LocalDateTime.now();

        // 获取所有活跃的预警
        List<Warning> activeWarnings = warningRepository.findByStatus(Warning.WarningStatus.ACTIVE);

        // 筛选出已过期的预警
        List<Warning> expiredWarnings = activeWarnings.stream()
                .filter(warning -> warning.getEndTime() != null && warning.getEndTime().isBefore(currentTime))
                .collect(Collectors.toList());

        // 更新过期预警的状态
        for (Warning warning : expiredWarnings) {
            warning.setStatus(Warning.WarningStatus.EXPIRED);
            warningRepository.save(warning);
            LoggerUtil.info("预警已标记为过期，预警ID: {}, 预警类型: {}, 地区: {}", 
                    warning.getId(), warning.getWarningType(), warning.getRegion());
        }

        LoggerUtil.info("预警过期处理完成，处理预警数: {}", expiredWarnings.size());

        return expiredWarnings.size();
    }

    /**
     * 取消预警
     * 将指定的预警状态更新为取消
     *
     * @param warningId 预警ID
     * @return 更新后的预警对象
     * @throws IllegalArgumentException 当预警不存在时抛出异常
     */
    public Warning cancelWarning(Long warningId) {
        LoggerUtil.info("取消预警，预警ID: {}", warningId);

        Warning warning = getWarningById(warningId);

        // 检查预警是否已过期或已取消
        if (warning.getStatus() == Warning.WarningStatus.EXPIRED) {
            LoggerUtil.warn("取消预警失败: 预警已过期，预警ID: {}", warningId);
            throw new IllegalArgumentException("已过期的预警无法取消");
        }

        if (warning.getStatus() == Warning.WarningStatus.CANCELLED) {
            LoggerUtil.warn("取消预警失败: 预警已取消，预警ID: {}", warningId);
            throw new IllegalArgumentException("预警已取消");
        }

        // 更新状态为取消
        warning.setStatus(Warning.WarningStatus.CANCELLED);
        Warning cancelledWarning = warningRepository.save(warning);

        LoggerUtil.info("预警取消成功，预警ID: {}", warningId);

        return cancelledWarning;
    }

    /**
     * 删除预警
     * 从数据库中删除指定的预警记录
     *
     * @param warningId 预警ID
     * @throws IllegalArgumentException 当预警不存在时抛出异常
     */
    public void deleteWarning(Long warningId) {
        LoggerUtil.info("删除预警，预警ID: {}", warningId);

        // 验证预警是否存在
        Warning warning = getWarningById(warningId);

        warningRepository.deleteById(warningId);
        LoggerUtil.info("预警删除成功，预警ID: {}", warningId);
    }

    /**
     * 检查指定地区是否有活跃的预警
     * 用于判断某个地区是否存在活跃的预警信息
     *
     * @param region 地区
     * @return 如果存在活跃预警返回true，否则返回false
     */
    public boolean hasActiveWarnings(String region) {
        LoggerUtil.info("检查地区是否有活跃预警，地区: {}", region);

        if (StringUtil.isBlank(region)) {
            return false;
        }

        List<Warning> activeWarnings = getWarningsByRegionAndStatus(region, Warning.WarningStatus.ACTIVE);
        boolean hasActive = !activeWarnings.isEmpty();

        LoggerUtil.info("地区活跃预警检查完成，地区: {}, 是否有活跃预警: {}", region, hasActive);

        return hasActive;
    }

    /**
     * 获取指定地区最严重的活跃预警
     * 按预警等级排序，返回最严重的预警
     *
     * @param region 地区
     * @return 最严重的活跃预警对象，如果没有活跃预警则返回null
     */
    public Warning getMostSevereActiveWarning(String region) {
        LoggerUtil.info("获取指定地区最严重的活跃预警，地区: {}", region);

        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取最严重预警失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        List<Warning> activeWarnings = getWarningsByRegionAndStatus(region, Warning.WarningStatus.ACTIVE);

        if (activeWarnings.isEmpty()) {
            LoggerUtil.info("地区没有活跃预警，地区: {}", region);
            return null;
        }

        // 按等级排序，返回最严重的
        Warning mostSevere = activeWarnings.stream()
                .max((w1, w2) -> w1.getSeverity().compareTo(w2.getSeverity()))
                .orElse(null);

        LoggerUtil.info("获取最严重的活跃预警成功，地区: {}, 预警ID: {}, 等级: {}", 
                region, mostSevere != null ? mostSevere.getId() : "无", 
                mostSevere != null ? mostSevere.getSeverity() : "无");

        return mostSevere;
    }

    /**
     * 获取指定地区的预警统计信息
     * 统计不同状态和等级的预警数量
     *
     * @param region 地区
     * @return 包含预警统计信息的字符串
     */
    public String getWarningStatistics(String region) {
        LoggerUtil.info("获取预警统计信息，地区: {}", region);

        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取预警统计失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        List<Warning> warnings = getWarningsByRegion(region);

        long activeCount = warnings.stream()
                .filter(w -> w.getStatus() == Warning.WarningStatus.ACTIVE)
                .count();

        long expiredCount = warnings.stream()
                .filter(w -> w.getStatus() == Warning.WarningStatus.EXPIRED)
                .count();

        long cancelledCount = warnings.stream()
                .filter(w -> w.getStatus() == Warning.WarningStatus.CANCELLED)
                .count();

        long criticalCount = warnings.stream()
                .filter(w -> w.getSeverity() == Warning.WarningSeverity.CRITICAL)
                .count();

        String statistics = String.format(
                "地区: %s, 活跃预警: %d, 过期预警: %d, 取消预警: %d, 严重预警: %d",
                region, activeCount, expiredCount, cancelledCount, criticalCount
        );

        LoggerUtil.info("预警统计信息: {}", statistics);

        return statistics;
    }
}
