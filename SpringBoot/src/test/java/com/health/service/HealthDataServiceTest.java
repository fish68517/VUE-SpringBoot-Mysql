package com.health.service;

import com.health.entity.HealthData;
import com.health.repository.HealthDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 健康数据服务单元测试
 * 测试健康数据的保存、查询、统计和趋势分析逻辑
 */
@ExtendWith(MockitoExtension.class)
public class HealthDataServiceTest {

    @Mock
    private HealthDataRepository healthDataRepository;

    @InjectMocks
    private HealthDataService healthDataService;

    private HealthData testHealthData;

    @BeforeEach
    public void setUp() {
        testHealthData = new HealthData();
        testHealthData.setId(1L);
        testHealthData.setUserId(1L);
        testHealthData.setHeight(new BigDecimal("170.5"));
        testHealthData.setWeight(new BigDecimal("70.0"));
        testHealthData.setHeartRate(72);
        testHealthData.setBloodPressure("120/80");
        testHealthData.setDietRecord("正常饮食");
        testHealthData.setExerciseRecord("跑步30分钟");
        testHealthData.setDataType("ROUTINE");
        testHealthData.setRecordedAt(LocalDateTime.now());
    }

    @Test
    public void testSaveHealthDataSuccess() {
        // 模拟保存
        when(healthDataRepository.save(any(HealthData.class))).thenReturn(testHealthData);

        // 执行保存
        HealthData result = healthDataService.saveHealthData(testHealthData);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        verify(healthDataRepository, times(1)).save(any(HealthData.class));
    }

    @Test
    public void testSaveHealthDataInvalidHeight() {
        // 准备无效的身高
        testHealthData.setHeight(new BigDecimal("49.9")); // 太低

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidWeight() {
        // 准备无效的体重
        testHealthData.setWeight(new BigDecimal("9.9")); // 太低

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidHeartRate() {
        // 准备无效的心率
        testHealthData.setHeartRate(29); // 太低

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataNullUserId() {
        // 准备无效的用户ID
        testHealthData.setUserId(null);

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testGetUserHealthDataSuccess() {
        // 准备健康数据列表
        List<HealthData> healthDataList = Arrays.asList(testHealthData);

        // 模拟查询
        when(healthDataRepository.findByUserId(1L)).thenReturn(healthDataList);

        // 执行查询
        List<HealthData> result = healthDataService.getUserHealthData(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetUserHealthDataNullUserId() {
        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getUserHealthData(null));
    }

    @Test
    public void testGetHealthDataByTimeRangeSuccess() {
        // 准备时间范围
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();

        // 准备健康数据列表
        List<HealthData> healthDataList = Arrays.asList(testHealthData);

        // 模拟查询
        when(healthDataRepository.findByUserIdAndTimeRange(1L, startTime, endTime)).thenReturn(healthDataList);

        // 执行查询
        List<HealthData> result = healthDataService.getHealthDataByTimeRange(1L, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findByUserIdAndTimeRange(1L, startTime, endTime);
    }

    @Test
    public void testGetHealthDataByTimeRangeInvalidRange() {
        // 准备无效的时间范围
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(7);

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> 
            healthDataService.getHealthDataByTimeRange(1L, startTime, endTime));
    }

    @Test
    public void testGetHealthDataByTypeSuccess() {
        // 准备健康数据列表
        List<HealthData> healthDataList = Arrays.asList(testHealthData);

        // 模拟查询
        when(healthDataRepository.findByUserIdAndDataType(1L, "ROUTINE")).thenReturn(healthDataList);

        // 执行查询
        List<HealthData> result = healthDataService.getHealthDataByType(1L, "ROUTINE");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findByUserIdAndDataType(1L, "ROUTINE");
    }

    @Test
    public void testGetLatestHealthDataSuccess() {
        // 准备健康数据列表
        List<HealthData> healthDataList = Arrays.asList(testHealthData);

        // 模拟查询
        when(healthDataRepository.findLatestByUserId(1L, 10)).thenReturn(healthDataList);

        // 执行查询
        List<HealthData> result = healthDataService.getLatestHealthData(1L, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findLatestByUserId(1L, 10);
    }

    @Test
    public void testGetLatestHealthDataInvalidLimit() {
        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getLatestHealthData(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getLatestHealthData(1L, -1));
    }

    @Test
    public void testGetHealthDataStatisticsSuccess() {
        // 准备多条健康数据
        HealthData data1 = new HealthData();
        data1.setUserId(1L);
        data1.setHeight(new BigDecimal("170"));
        data1.setWeight(new BigDecimal("70"));
        data1.setHeartRate(72);
        data1.setRecordedAt(LocalDateTime.now());

        HealthData data2 = new HealthData();
        data2.setUserId(1L);
        data2.setHeight(new BigDecimal("170"));
        data2.setWeight(new BigDecimal("71"));
        data2.setHeartRate(74);
        data2.setRecordedAt(LocalDateTime.now());

        List<HealthData> healthDataList = Arrays.asList(data1, data2);

        // 模拟查询
        when(healthDataRepository.findByUserId(1L)).thenReturn(healthDataList);

        // 执行统计
        HealthDataService.HealthDataStatistics result = healthDataService.getHealthDataStatistics(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getTotalRecords());
        assertNotNull(result.getAverageHeight());
        assertNotNull(result.getAverageWeight());
        assertNotNull(result.getAverageHeartRate());
        verify(healthDataRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetHealthDataStatisticsEmptyData() {
        // 模拟查询返回空列表
        when(healthDataRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // 执行统计
        HealthDataService.HealthDataStatistics result = healthDataService.getHealthDataStatistics(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(0, result.getTotalRecords());
        assertNull(result.getAverageHeight());
        assertNull(result.getAverageWeight());
    }

    @Test
    public void testGetHealthDataStatisticsNullUserId() {
        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getHealthDataStatistics(null));
    }

    @Test
    public void testSaveHealthDataWithMaxValues() {
        // 准备边界值数据
        testHealthData.setHeight(new BigDecimal("250"));
        testHealthData.setWeight(new BigDecimal("500"));
        testHealthData.setHeartRate(200);

        // 模拟保存
        when(healthDataRepository.save(any(HealthData.class))).thenReturn(testHealthData);

        // 执行保存
        HealthData result = healthDataService.saveHealthData(testHealthData);

        // 验证结果
        assertNotNull(result);
        verify(healthDataRepository, times(1)).save(any(HealthData.class));
    }

    @Test
    public void testSaveHealthDataWithMinValues() {
        // 准备边界值数据
        testHealthData.setHeight(new BigDecimal("50"));
        testHealthData.setWeight(new BigDecimal("10"));
        testHealthData.setHeartRate(30);

        // 模拟保存
        when(healthDataRepository.save(any(HealthData.class))).thenReturn(testHealthData);

        // 执行保存
        HealthData result = healthDataService.saveHealthData(testHealthData);

        // 验证结果
        assertNotNull(result);
        verify(healthDataRepository, times(1)).save(any(HealthData.class));
    }
}
