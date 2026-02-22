package com.agricultural.repository;

import com.agricultural.entity.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预警数据访问层测试
 * 
 * 测试WarningRepository的CRUD操作和自定义查询方法
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class WarningRepositoryTest {

    @Autowired
    private WarningRepository warningRepository;

    private Warning testWarning;

    @BeforeEach
    void setUp() {
        // 创建测试预警
        testWarning = Warning.builder()
                .warningType("暴雨")
                .region("河南省郑州市")
                .severity(Warning.WarningSeverity.HIGH)
                .description("未来24小时内可能出现暴雨，请做好防护准备")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(24))
                .status(Warning.WarningStatus.ACTIVE)
                .build();
    }

    @Test
    void testSaveWarning() {
        // 保存预警
        Warning savedWarning = warningRepository.save(testWarning);
        
        // 验证预警已保存
        assertNotNull(savedWarning.getId());
        assertEquals("暴雨", savedWarning.getWarningType());
        assertEquals("河南省郑州市", savedWarning.getRegion());
        assertEquals(Warning.WarningSeverity.HIGH, savedWarning.getSeverity());
        assertNotNull(savedWarning.getCreatedAt());
        assertNotNull(savedWarning.getUpdatedAt());
    }

    @Test
    void testFindWarningById() {
        // 保存预警
        Warning savedWarning = warningRepository.save(testWarning);
        
        // 根据ID查询预警
        Optional<Warning> foundWarning = warningRepository.findById(savedWarning.getId());
        
        // 验证查询结果
        assertTrue(foundWarning.isPresent());
        assertEquals("暴雨", foundWarning.get().getWarningType());
    }

    @Test
    void testFindByRegion() {
        // 保存预警
        warningRepository.save(testWarning);
        
        Warning anotherWarning = Warning.builder()
                .warningType("冰雹")
                .region("河南省郑州市")
                .severity(Warning.WarningSeverity.CRITICAL)
                .description("预计明天下午可能出现冰雹")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(12))
                .status(Warning.WarningStatus.ACTIVE)
                .build();
        warningRepository.save(anotherWarning);
        
        // 根据地区查询预警
        List<Warning> warnings = warningRepository.findByRegion("河南省郑州市");
        
        // 验证查询结果
        assertTrue(warnings.size() >= 2);
        assertTrue(warnings.stream().allMatch(w -> "河南省郑州市".equals(w.getRegion())));
    }

    @Test
    void testFindByWarningType() {
        // 保存多个预警
        warningRepository.save(testWarning);
        
        Warning anotherRainWarning = Warning.builder()
                .warningType("暴雨")
                .region("山东省济南市")
                .severity(Warning.WarningSeverity.MEDIUM)
                .description("预计明天有暴雨")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(24))
                .status(Warning.WarningStatus.ACTIVE)
                .build();
        warningRepository.save(anotherRainWarning);
        
        // 根据预警类型查询
        List<Warning> rainWarnings = warningRepository.findByWarningType("暴雨");
        
        // 验证查询结果
        assertTrue(rainWarnings.size() >= 2);
        assertTrue(rainWarnings.stream().allMatch(w -> "暴雨".equals(w.getWarningType())));
    }

    @Test
    void testFindByStatus() {
        // 保存预警
        warningRepository.save(testWarning);
        
        Warning expiredWarning = Warning.builder()
                .warningType("大风")
                .region("北京市朝阳区")
                .severity(Warning.WarningSeverity.MEDIUM)
                .description("大风预警已过期")
                .startTime(LocalDateTime.now().minusHours(48))
                .endTime(LocalDateTime.now().minusHours(24))
                .status(Warning.WarningStatus.EXPIRED)
                .build();
        warningRepository.save(expiredWarning);
        
        // 查询活跃预警
        List<Warning> activeWarnings = warningRepository.findByStatus(Warning.WarningStatus.ACTIVE);
        
        // 验证查询结果
        assertTrue(activeWarnings.size() >= 1);
        assertTrue(activeWarnings.stream().allMatch(w -> w.getStatus() == Warning.WarningStatus.ACTIVE));
    }

    @Test
    void testFindByRegionAndWarningType() {
        // 保存预警
        warningRepository.save(testWarning);
        
        Warning differentTypeWarning = Warning.builder()
                .warningType("冰雹")
                .region("河南省郑州市")
                .severity(Warning.WarningSeverity.HIGH)
                .description("冰雹预警")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(12))
                .status(Warning.WarningStatus.ACTIVE)
                .build();
        warningRepository.save(differentTypeWarning);
        
        // 根据地区和预警类型查询
        List<Warning> warnings = warningRepository.findByRegionAndWarningType("河南省郑州市", "暴雨");
        
        // 验证查询结果
        assertTrue(warnings.size() >= 1);
        assertTrue(warnings.stream().allMatch(w -> 
            "河南省郑州市".equals(w.getRegion()) && "暴雨".equals(w.getWarningType())));
    }

    @Test
    void testFindByRegionAndStatus() {
        // 保存预警
        warningRepository.save(testWarning);
        
        Warning expiredWarning = Warning.builder()
                .warningType("暴雨")
                .region("河南省郑州市")
                .severity(Warning.WarningSeverity.MEDIUM)
                .description("已过期的暴雨预警")
                .startTime(LocalDateTime.now().minusHours(48))
                .endTime(LocalDateTime.now().minusHours(24))
                .status(Warning.WarningStatus.EXPIRED)
                .build();
        warningRepository.save(expiredWarning);
        
        // 根据地区和状态查询
        List<Warning> activeWarnings = warningRepository.findByRegionAndStatus("河南省郑州市", Warning.WarningStatus.ACTIVE);
        
        // 验证查询结果
        assertTrue(activeWarnings.size() >= 1);
        assertTrue(activeWarnings.stream().allMatch(w -> 
            "河南省郑州市".equals(w.getRegion()) && w.getStatus() == Warning.WarningStatus.ACTIVE));
    }

    @Test
    void testFindByRegionAndWarningTypeAndStatus() {
        // 保存预警
        warningRepository.save(testWarning);
        
        Warning differentWarning = Warning.builder()
                .warningType("冰雹")
                .region("河南省郑州市")
                .severity(Warning.WarningSeverity.CRITICAL)
                .description("冰雹预警")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(12))
                .status(Warning.WarningStatus.ACTIVE)
                .build();
        warningRepository.save(differentWarning);
        
        // 根据地区、预警类型和状态查询
        List<Warning> warnings = warningRepository.findByRegionAndWarningTypeAndStatus(
            "河南省郑州市", "暴雨", Warning.WarningStatus.ACTIVE);
        
        // 验证查询结果
        assertTrue(warnings.size() >= 1);
        assertTrue(warnings.stream().allMatch(w -> 
            "河南省郑州市".equals(w.getRegion()) && 
            "暴雨".equals(w.getWarningType()) && 
            w.getStatus() == Warning.WarningStatus.ACTIVE));
    }

    @Test
    void testFindActiveWarnings() {
        // 保存活跃预警
        warningRepository.save(testWarning);
        
        // 保存已过期的预警
        Warning expiredWarning = Warning.builder()
                .warningType("大风")
                .region("北京市朝阳区")
                .severity(Warning.WarningSeverity.MEDIUM)
                .description("已过期的大风预警")
                .startTime(LocalDateTime.now().minusHours(48))
                .endTime(LocalDateTime.now().minusHours(24))
                .status(Warning.WarningStatus.EXPIRED)
                .build();
        warningRepository.save(expiredWarning);
        
        // 查询活跃预警
        List<Warning> activeWarnings = warningRepository.findActiveWarnings(LocalDateTime.now());
        
        // 验证查询结果
        assertTrue(activeWarnings.size() >= 1);
        assertTrue(activeWarnings.stream().allMatch(w -> w.getStatus() == Warning.WarningStatus.ACTIVE));
    }

    @Test
    void testUpdateWarning() {
        // 保存预警
        Warning savedWarning = warningRepository.save(testWarning);
        Long warningId = savedWarning.getId();
        
        // 更新预警信息
        savedWarning.setDescription("更新的预警描述");
        savedWarning.setSeverity(Warning.WarningSeverity.CRITICAL);
        Warning updatedWarning = warningRepository.save(savedWarning);
        
        // 验证更新结果
        assertEquals("更新的预警描述", updatedWarning.getDescription());
        assertEquals(Warning.WarningSeverity.CRITICAL, updatedWarning.getSeverity());
        assertNotNull(updatedWarning.getUpdatedAt());
    }

    @Test
    void testDeleteWarning() {
        // 保存预警
        Warning savedWarning = warningRepository.save(testWarning);
        Long warningId = savedWarning.getId();
        
        // 删除预警
        warningRepository.deleteById(warningId);
        
        // 验证预警已删除
        Optional<Warning> deletedWarning = warningRepository.findById(warningId);
        assertFalse(deletedWarning.isPresent());
    }

    @Test
    void testWarningPrePersist() {
        // 创建预警（不设置时间戳）
        Warning warning = Warning.builder()
                .warningType("霜冻")
                .region("江苏省南京市")
                .severity(Warning.WarningSeverity.HIGH)
                .description("霜冻预警")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(12))
                .build();
        
        // 保存预警
        Warning savedWarning = warningRepository.save(warning);
        
        // 验证时间戳已自动设置
        assertNotNull(savedWarning.getCreatedAt());
        assertNotNull(savedWarning.getUpdatedAt());
        assertEquals(Warning.WarningStatus.ACTIVE, savedWarning.getStatus());
    }

    @Test
    void testWarningPreUpdate() {
        // 保存预警
        Warning savedWarning = warningRepository.save(testWarning);
        LocalDateTime originalUpdatedAt = savedWarning.getUpdatedAt();
        
        // 等待一段时间后更新
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 更新预警
        savedWarning.setStatus(Warning.WarningStatus.EXPIRED);
        Warning updatedWarning = warningRepository.save(savedWarning);
        
        // 验证updatedAt已更新
        assertNotNull(updatedWarning.getUpdatedAt());
        assertTrue(updatedWarning.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   updatedWarning.getUpdatedAt().isEqual(originalUpdatedAt));
    }
}
