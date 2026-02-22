package com.agricultural.repository;

import com.agricultural.entity.Crop;
import com.agricultural.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 作物数据访问层测试
 * 
 * 测试CropRepository的CRUD操作和自定义查询方法
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class CropRepositoryTest {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    private Crop testCrop;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = User.builder()
                .username("test_farmer")
                .password("encrypted_password")
                .email("farmer@example.com")
                .phone("13800138000")
                .userType(User.UserType.FARMER)
                .region("河南省郑州市")
                .status(User.UserStatus.ACTIVE)
                .build();
        testUser = userRepository.save(testUser);

        // 创建测试作物
        testCrop = Crop.builder()
                .cropName("小麦")
                .growthStage("拔节期")
                .region("河南省郑州市")
                .userId(testUser.getId())
                .plantingDate(LocalDate.of(2023, 10, 15))
                .expectedHarvestDate(LocalDate.of(2024, 6, 1))
                .build();
    }

    @Test
    void testSaveCrop() {
        // 保存作物
        Crop savedCrop = cropRepository.save(testCrop);
        
        // 验证作物已保存
        assertNotNull(savedCrop.getId());
        assertEquals("小麦", savedCrop.getCropName());
        assertEquals("拔节期", savedCrop.getGrowthStage());
        assertNotNull(savedCrop.getCreatedAt());
    }

    @Test
    void testFindCropById() {
        // 保存作物
        Crop savedCrop = cropRepository.save(testCrop);
        
        // 根据ID查询作物
        Optional<Crop> foundCrop = cropRepository.findById(savedCrop.getId());
        
        // 验证查询结果
        assertTrue(foundCrop.isPresent());
        assertEquals("小麦", foundCrop.get().getCropName());
    }

    @Test
    void testFindByUserId() {
        // 保存作物
        cropRepository.save(testCrop);
        
        Crop anotherCrop = Crop.builder()
                .cropName("玉米")
                .growthStage("苗期")
                .region("河南省郑州市")
                .userId(testUser.getId())
                .plantingDate(LocalDate.of(2024, 4, 1))
                .expectedHarvestDate(LocalDate.of(2024, 10, 15))
                .build();
        cropRepository.save(anotherCrop);
        
        // 根据用户ID查询作物
        List<Crop> userCrops = cropRepository.findByUserId(testUser.getId());
        
        // 验证查询结果
        assertEquals(2, userCrops.size());
        assertTrue(userCrops.stream().allMatch(c -> c.getUserId().equals(testUser.getId())));
    }

    @Test
    void testFindByRegion() {
        // 保存作物
        cropRepository.save(testCrop);
        
        Crop anotherCrop = Crop.builder()
                .cropName("水稻")
                .growthStage("分蘖期")
                .region("河南省郑州市")
                .userId(testUser.getId())
                .plantingDate(LocalDate.of(2024, 5, 1))
                .expectedHarvestDate(LocalDate.of(2024, 10, 1))
                .build();
        cropRepository.save(anotherCrop);
        
        // 根据地区查询作物
        List<Crop> regionCrops = cropRepository.findByRegion("河南省郑州市");
        
        // 验证查询结果
        assertTrue(regionCrops.size() >= 2);
        assertTrue(regionCrops.stream().allMatch(c -> "河南省郑州市".equals(c.getRegion())));
    }

    @Test
    void testFindByCropName() {
        // 保存作物
        cropRepository.save(testCrop);
        
        // 根据作物名称查询
        List<Crop> wheatCrops = cropRepository.findByCropName("小麦");
        
        // 验证查询结果
        assertTrue(wheatCrops.size() >= 1);
        assertTrue(wheatCrops.stream().allMatch(c -> "小麦".equals(c.getCropName())));
    }

    @Test
    void testFindByGrowthStage() {
        // 保存作物
        cropRepository.save(testCrop);
        
        Crop anotherCrop = Crop.builder()
                .cropName("玉米")
                .growthStage("拔节期")
                .region("山东省济南市")
                .userId(testUser.getId())
                .plantingDate(LocalDate.of(2024, 4, 1))
                .expectedHarvestDate(LocalDate.of(2024, 10, 15))
                .build();
        cropRepository.save(anotherCrop);
        
        // 根据生育期查询作物
        List<Crop> stageCrops = cropRepository.findByGrowthStage("拔节期");
        
        // 验证查询结果
        assertTrue(stageCrops.size() >= 2);
        assertTrue(stageCrops.stream().allMatch(c -> "拔节期".equals(c.getGrowthStage())));
    }

    @Test
    void testFindByUserIdAndRegion() {
        // 保存作物
        cropRepository.save(testCrop);
        
        // 创建另一个用户和作物
        User anotherUser = User.builder()
                .username("another_farmer")
                .password("password")
                .userType(User.UserType.FARMER)
                .region("山东省济南市")
                .status(User.UserStatus.ACTIVE)
                .build();
        anotherUser = userRepository.save(anotherUser);
        
        Crop anotherCrop = Crop.builder()
                .cropName("玉米")
                .growthStage("苗期")
                .region("山东省济南市")
                .userId(anotherUser.getId())
                .plantingDate(LocalDate.of(2024, 4, 1))
                .expectedHarvestDate(LocalDate.of(2024, 10, 15))
                .build();
        cropRepository.save(anotherCrop);
        
        // 根据用户ID和地区查询作物
        List<Crop> userRegionCrops = cropRepository.findByUserIdAndRegion(testUser.getId(), "河南省郑州市");
        
        // 验证查询结果
        assertTrue(userRegionCrops.size() >= 1);
        assertTrue(userRegionCrops.stream().allMatch(c -> 
            c.getUserId().equals(testUser.getId()) && "河南省郑州市".equals(c.getRegion())));
    }

    @Test
    void testUpdateCrop() {
        // 保存作物
        Crop savedCrop = cropRepository.save(testCrop);
        Long cropId = savedCrop.getId();
        
        // 更新作物信息
        savedCrop.setGrowthStage("抽穗期");
        savedCrop.setExpectedHarvestDate(LocalDate.of(2024, 6, 15));
        Crop updatedCrop = cropRepository.save(savedCrop);
        
        // 验证更新结果
        assertEquals("抽穗期", updatedCrop.getGrowthStage());
        assertEquals(LocalDate.of(2024, 6, 15), updatedCrop.getExpectedHarvestDate());
    }

    @Test
    void testDeleteCrop() {
        // 保存作物
        Crop savedCrop = cropRepository.save(testCrop);
        Long cropId = savedCrop.getId();
        
        // 删除作物
        cropRepository.deleteById(cropId);
        
        // 验证作物已删除
        Optional<Crop> deletedCrop = cropRepository.findById(cropId);
        assertFalse(deletedCrop.isPresent());
    }

    @Test
    void testCropPrePersist() {
        // 创建作物（不设置时间戳）
        Crop crop = Crop.builder()
                .cropName("大豆")
                .growthStage("开花期")
                .region("河南省郑州市")
                .userId(testUser.getId())
                .plantingDate(LocalDate.of(2024, 5, 15))
                .expectedHarvestDate(LocalDate.of(2024, 10, 30))
                .build();
        
        // 保存作物
        Crop savedCrop = cropRepository.save(crop);
        
        // 验证时间戳已自动设置
        assertNotNull(savedCrop.getCreatedAt());
    }
}
