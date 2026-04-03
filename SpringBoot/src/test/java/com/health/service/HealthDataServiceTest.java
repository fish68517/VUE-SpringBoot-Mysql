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
        testHealthData.setBodyTemperature(new BigDecimal("36.5"));
        testHealthData.setBloodOxygen(98);
        testHealthData.setBloodSugar(new BigDecimal("5.4"));
        testHealthData.setSleepDuration(new BigDecimal("7.5"));
        testHealthData.setDietRecord("normal");
        testHealthData.setExerciseRecord("running");
        testHealthData.setDataType("ROUTINE");
        testHealthData.setRecordedAt(LocalDateTime.now());
    }

    @Test
    public void testSaveHealthDataSuccess() {
        when(healthDataRepository.save(any(HealthData.class))).thenReturn(testHealthData);
        HealthData result = healthDataService.saveHealthData(testHealthData);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        verify(healthDataRepository, times(1)).save(any(HealthData.class));
    }

    @Test
    public void testSaveHealthDataInvalidHeight() {
        testHealthData.setHeight(new BigDecimal("49.9"));
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidWeight() {
        testHealthData.setWeight(new BigDecimal("9.9"));
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidHeartRate() {
        testHealthData.setHeartRate(29);
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidBodyTemperature() {
        testHealthData.setBodyTemperature(new BigDecimal("43"));
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidBloodOxygen() {
        testHealthData.setBloodOxygen(101);
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidBloodSugar() {
        testHealthData.setBloodSugar(new BigDecimal("31"));
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataInvalidSleepDuration() {
        testHealthData.setSleepDuration(new BigDecimal("25"));
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testSaveHealthDataNullUserId() {
        testHealthData.setUserId(null);
        assertThrows(IllegalArgumentException.class, () -> healthDataService.saveHealthData(testHealthData));
    }

    @Test
    public void testGetUserHealthDataSuccess() {
        List<HealthData> healthDataList = Arrays.asList(testHealthData);
        when(healthDataRepository.findByUserId(1L)).thenReturn(healthDataList);

        List<HealthData> result = healthDataService.getUserHealthData(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetUserHealthDataNullUserId() {
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getUserHealthData(null));
    }

    @Test
    public void testGetHealthDataByTimeRangeSuccess() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<HealthData> healthDataList = Arrays.asList(testHealthData);
        when(healthDataRepository.findByUserIdAndTimeRange(1L, startTime, endTime)).thenReturn(healthDataList);

        List<HealthData> result = healthDataService.getHealthDataByTimeRange(1L, startTime, endTime);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findByUserIdAndTimeRange(1L, startTime, endTime);
    }

    @Test
    public void testGetHealthDataByTimeRangeInvalidRange() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(7);
        assertThrows(IllegalArgumentException.class,
                () -> healthDataService.getHealthDataByTimeRange(1L, startTime, endTime));
    }

    @Test
    public void testGetHealthDataByTypeSuccess() {
        List<HealthData> healthDataList = Arrays.asList(testHealthData);
        when(healthDataRepository.findByUserIdAndDataType(1L, "ROUTINE")).thenReturn(healthDataList);

        List<HealthData> result = healthDataService.getHealthDataByType(1L, "ROUTINE");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findByUserIdAndDataType(1L, "ROUTINE");
    }

    @Test
    public void testGetLatestHealthDataSuccess() {
        List<HealthData> healthDataList = Arrays.asList(testHealthData);
        when(healthDataRepository.findLatestByUserId(1L, 10)).thenReturn(healthDataList);

        List<HealthData> result = healthDataService.getLatestHealthData(1L, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthDataRepository, times(1)).findLatestByUserId(1L, 10);
    }

    @Test
    public void testGetLatestHealthDataInvalidLimit() {
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getLatestHealthData(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getLatestHealthData(1L, -1));
    }

    @Test
    public void testGetHealthDataStatisticsSuccess() {
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
        when(healthDataRepository.findByUserId(1L)).thenReturn(healthDataList);

        HealthDataService.HealthDataStatistics result = healthDataService.getHealthDataStatistics(1L);

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
        when(healthDataRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        HealthDataService.HealthDataStatistics result = healthDataService.getHealthDataStatistics(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(0, result.getTotalRecords());
        assertNull(result.getAverageHeight());
        assertNull(result.getAverageWeight());
    }

    @Test
    public void testGetHealthDataStatisticsNullUserId() {
        assertThrows(IllegalArgumentException.class, () -> healthDataService.getHealthDataStatistics(null));
    }

    @Test
    public void testSaveHealthDataWithMaxValues() {
        testHealthData.setHeight(new BigDecimal("250"));
        testHealthData.setWeight(new BigDecimal("500"));
        testHealthData.setHeartRate(200);
        testHealthData.setBodyTemperature(new BigDecimal("42"));
        testHealthData.setBloodOxygen(100);
        testHealthData.setBloodSugar(new BigDecimal("30"));
        testHealthData.setSleepDuration(new BigDecimal("24"));
        when(healthDataRepository.save(any(HealthData.class))).thenReturn(testHealthData);

        HealthData result = healthDataService.saveHealthData(testHealthData);

        assertNotNull(result);
        verify(healthDataRepository, times(1)).save(any(HealthData.class));
    }

    @Test
    public void testSaveHealthDataWithMinValues() {
        testHealthData.setHeight(new BigDecimal("50"));
        testHealthData.setWeight(new BigDecimal("10"));
        testHealthData.setHeartRate(30);
        testHealthData.setBodyTemperature(new BigDecimal("34"));
        testHealthData.setBloodOxygen(70);
        testHealthData.setBloodSugar(new BigDecimal("2"));
        testHealthData.setSleepDuration(BigDecimal.ZERO);
        when(healthDataRepository.save(any(HealthData.class))).thenReturn(testHealthData);

        HealthData result = healthDataService.saveHealthData(testHealthData);

        assertNotNull(result);
        verify(healthDataRepository, times(1)).save(any(HealthData.class));
    }
}
