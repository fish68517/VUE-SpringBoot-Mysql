package com.agricultural.service;

import com.agricultural.entity.Crop;
import com.agricultural.repository.CropRepository;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 作物业务逻辑层
 * 处理作物相关的业务逻辑，包括添加作物、获取用户作物列表、更新作物信息、删除作物等操作
 */
@Service
@Transactional
public class CropService {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserService userService;

    /**
     * 添加作物
     * 创建新的作物记录
     *
     * @param cropName 作物名称
     * @param growthStage 生育期
     * @param region 地区
     * @param userId 用户ID
     * @param plantingDate 播种日期
     * @param expectedHarvestDate 预期收获日期
     * @return 创建的作物对象
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public Crop addCrop(String cropName, String growthStage, String region, Long userId,
                        LocalDate plantingDate, LocalDate expectedHarvestDate) {
        LoggerUtil.info("开始添加作物，作物名称: {}, 用户ID: {}, 地区: {}", cropName, userId, region);

        // 验证作物名称
        if (StringUtil.isBlank(cropName)) {
            LoggerUtil.warn("添加作物失败: 作物名称为空");
            throw new IllegalArgumentException("作物名称不能为空");
        }

        if (cropName.length() > 50) {
            LoggerUtil.warn("添加作物失败: 作物名称过长，作物名称: {}", cropName);
            throw new IllegalArgumentException("作物名称长度不能超过50个字符");
        }

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("添加作物失败: 用户ID无效，用户ID: {}", userId);
            throw new IllegalArgumentException("用户ID无效");
        }

        // 验证用户是否存在
        if (!userService.validateUser(userId)) {
            LoggerUtil.warn("添加作物失败: 用户不存在或状态异常，用户ID: {}", userId);
            throw new IllegalArgumentException("用户不存在或状态异常");
        }

        // 验证地区
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("添加作物失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        if (region.length() > 100) {
            LoggerUtil.warn("添加作物失败: 地区名称过长，地区: {}", region);
            throw new IllegalArgumentException("地区名称长度不能超过100个字符");
        }

        // 验证生育期
        if (StringUtil.isNotBlank(growthStage) && growthStage.length() > 50) {
            LoggerUtil.warn("添加作物失败: 生育期过长，生育期: {}", growthStage);
            throw new IllegalArgumentException("生育期长度不能超过50个字符");
        }

        // 验证播种日期和预期收获日期
        if (plantingDate != null && expectedHarvestDate != null) {
            if (expectedHarvestDate.isBefore(plantingDate)) {
                LoggerUtil.warn("添加作物失败: 预期收获日期早于播种日期，用户ID: {}", userId);
                throw new IllegalArgumentException("预期收获日期不能早于播种日期");
            }
        }

        // 创建作物对象
        Crop crop = Crop.builder()
                .cropName(cropName)
                .growthStage(growthStage)
                .region(region)
                .userId(userId)
                .plantingDate(plantingDate)
                .expectedHarvestDate(expectedHarvestDate)
                .build();

        // 保存作物
        Crop savedCrop = cropRepository.save(crop);
        LoggerUtil.info("作物添加成功，作物ID: {}, 作物名称: {}, 用户ID: {}", 
                savedCrop.getId(), cropName, userId);

        return savedCrop;
    }

    /**
     * 获取用户作物列表
     * 获取指定用户的所有作物信息
     *
     * @param userId 用户ID
     * @return 作物列表
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public List<Crop> getUserCropList(Long userId) {
        LoggerUtil.info("获取用户作物列表，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取用户作物列表失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        // 验证用户是否存在
        if (!userService.validateUser(userId)) {
            LoggerUtil.warn("获取用户作物列表失败: 用户不存在或状态异常，用户ID: {}", userId);
            throw new IllegalArgumentException("用户不存在或状态异常");
        }

        List<Crop> crops = cropRepository.findByUserId(userId);
        LoggerUtil.info("获取用户作物列表成功，用户ID: {}, 作物总数: {}", userId, crops.size());

        return crops;
    }

    /**
     * 根据作物ID获取作物详情
     * 获取指定ID的作物信息
     *
     * @param cropId 作物ID
     * @return 作物对象
     * @throws IllegalArgumentException 当作物不存在时抛出异常
     */
    public Crop getCropById(Long cropId) {
        LoggerUtil.info("根据ID获取作物详情，作物ID: {}", cropId);

        // 验证作物ID
        if (cropId == null || cropId <= 0) {
            LoggerUtil.warn("获取作物失败: 作物ID无效");
            throw new IllegalArgumentException("作物ID无效");
        }

        Optional<Crop> cropOptional = cropRepository.findById(cropId);
        if (!cropOptional.isPresent()) {
            LoggerUtil.warn("获取作物失败: 作物不存在，作物ID: {}", cropId);
            throw new IllegalArgumentException("作物不存在");
        }

        LoggerUtil.info("根据ID获取作物详情成功，作物ID: {}", cropId);

        return cropOptional.get();
    }

    /**
     * 根据地区获取作物列表
     * 获取指定地区的所有作物信息
     *
     * @param region 地区
     * @return 作物列表
     * @throws IllegalArgumentException 当地区为空时抛出异常
     */
    public List<Crop> getCropsByRegion(String region) {
        LoggerUtil.info("根据地区获取作物列表，地区: {}", region);

        // 验证地区参数
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取作物列表失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        List<Crop> crops = cropRepository.findByRegion(region);
        LoggerUtil.info("根据地区获取作物列表成功，地区: {}, 作物数: {}", region, crops.size());

        return crops;
    }

    /**
     * 根据作物名称获取作物列表
     * 获取指定名称的所有作物信息
     *
     * @param cropName 作物名称
     * @return 作物列表
     * @throws IllegalArgumentException 当作物名称为空时抛出异常
     */
    public List<Crop> getCropsByName(String cropName) {
        LoggerUtil.info("根据作物名称获取作物列表，作物名称: {}", cropName);

        // 验证作物名称
        if (StringUtil.isBlank(cropName)) {
            LoggerUtil.warn("获取作物列表失败: 作物名称为空");
            throw new IllegalArgumentException("作物名称不能为空");
        }

        List<Crop> crops = cropRepository.findByCropName(cropName);
        LoggerUtil.info("根据作物名称获取作物列表成功，作物名称: {}, 作物数: {}", cropName, crops.size());

        return crops;
    }

    /**
     * 根据生育期获取作物列表
     * 获取指定生育期的所有作物信息
     *
     * @param growthStage 生育期
     * @return 作物列表
     * @throws IllegalArgumentException 当生育期为空时抛出异常
     */
    public List<Crop> getCropsByGrowthStage(String growthStage) {
        LoggerUtil.info("根据生育期获取作物列表，生育期: {}", growthStage);

        // 验证生育期参数
        if (StringUtil.isBlank(growthStage)) {
            LoggerUtil.warn("获取作物列表失败: 生育期为空");
            throw new IllegalArgumentException("生育期不能为空");
        }

        List<Crop> crops = cropRepository.findByGrowthStage(growthStage);
        LoggerUtil.info("根据生育期获取作物列表成功，生育期: {}, 作物数: {}", growthStage, crops.size());

        return crops;
    }

    /**
     * 根据用户ID和地区获取作物列表
     * 获取指定用户在指定地区的所有作物信息
     *
     * @param userId 用户ID
     * @param region 地区
     * @return 作物列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<Crop> getCropsByUserAndRegion(Long userId, String region) {
        LoggerUtil.info("根据用户ID和地区获取作物列表，用户ID: {}, 地区: {}", userId, region);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取作物列表失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        // 验证地区
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取作物列表失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        List<Crop> crops = cropRepository.findByUserIdAndRegion(userId, region);
        LoggerUtil.info("根据用户ID和地区获取作物列表成功，用户ID: {}, 地区: {}, 作物数: {}", 
                userId, region, crops.size());

        return crops;
    }

    /**
     * 获取所有作物列表
     * 获取系统中所有作物信息
     *
     * @return 作物列表
     */
    public List<Crop> getAllCrops() {
        LoggerUtil.info("获取所有作物列表");

        List<Crop> crops = cropRepository.findAll();
        LoggerUtil.info("获取所有作物列表成功，作物总数: {}", crops.size());

        return crops;
    }

    /**
     * 更新作物信息
     * 支持更新作物名称、生育期、地区、播种日期、预期收获日期等信息
     *
     * @param cropId 作物ID
     * @param cropName 作物名称
     * @param growthStage 生育期
     * @param region 地区
     * @param plantingDate 播种日期
     * @param expectedHarvestDate 预期收获日期
     * @return 更新后的作物对象
     * @throws IllegalArgumentException 当作物不存在或参数无效时抛出异常
     */
    public Crop updateCrop(Long cropId, String cropName, String growthStage, String region,
                           LocalDate plantingDate, LocalDate expectedHarvestDate) {
        LoggerUtil.info("开始更新作物信息，作物ID: {}", cropId);

        // 获取作物
        Crop crop = getCropById(cropId);

        // 验证并更新作物名称
        if (StringUtil.isNotBlank(cropName)) {
            if (cropName.length() > 50) {
                LoggerUtil.warn("更新作物失败: 作物名称过长，作物ID: {}", cropId);
                throw new IllegalArgumentException("作物名称长度不能超过50个字符");
            }
            crop.setCropName(cropName);
        }

        // 验证并更新生育期
        if (StringUtil.isNotBlank(growthStage)) {
            if (growthStage.length() > 50) {
                LoggerUtil.warn("更新作物失败: 生育期过长，作物ID: {}", cropId);
                throw new IllegalArgumentException("生育期长度不能超过50个字符");
            }
            crop.setGrowthStage(growthStage);
        }

        // 验证并更新地区
        if (StringUtil.isNotBlank(region)) {
            if (region.length() > 100) {
                LoggerUtil.warn("更新作物失败: 地区名称过长，作物ID: {}", cropId);
                throw new IllegalArgumentException("地区名称长度不能超过100个字符");
            }
            crop.setRegion(region);
        }

        // 验证并更新播种日期和预期收获日期
        if (plantingDate != null) {
            crop.setPlantingDate(plantingDate);
        }

        if (expectedHarvestDate != null) {
            crop.setExpectedHarvestDate(expectedHarvestDate);
        }

        // 验证播种日期和预期收获日期的逻辑关系
        if (crop.getPlantingDate() != null && crop.getExpectedHarvestDate() != null) {
            if (crop.getExpectedHarvestDate().isBefore(crop.getPlantingDate())) {
                LoggerUtil.warn("更新作物失败: 预期收获日期早于播种日期，作物ID: {}", cropId);
                throw new IllegalArgumentException("预期收获日期不能早于播种日期");
            }
        }

        Crop updatedCrop = cropRepository.save(crop);
        LoggerUtil.info("作物信息更新成功，作物ID: {}", cropId);

        return updatedCrop;
    }

    /**
     * 删除作物
     * 从数据库中删除指定的作物记录
     *
     * @param cropId 作物ID
     * @throws IllegalArgumentException 当作物不存在时抛出异常
     */
    public void deleteCrop(Long cropId) {
        LoggerUtil.info("开始删除作物，作物ID: {}", cropId);

        // 验证作物是否存在
        Crop crop = getCropById(cropId);

        cropRepository.deleteById(cropId);
        LoggerUtil.info("作物删除成功，作物ID: {}, 作物名称: {}", cropId, crop.getCropName());
    }

    /**
     * 检查作物是否存在
     * 用于验证作物是否存在
     *
     * @param cropId 作物ID
     * @return 如果作物存在返回true，否则返回false
     */
    public boolean cropExists(Long cropId) {
        LoggerUtil.info("检查作物是否存在，作物ID: {}", cropId);

        if (cropId == null || cropId <= 0) {
            return false;
        }

        return cropRepository.existsById(cropId);
    }

    /**
     * 获取用户作物总数
     * 获取指定用户的作物数量
     *
     * @param userId 用户ID
     * @return 作物总数
     * @throws IllegalArgumentException 当用户ID无效时抛出异常
     */
    public long getUserCropCount(Long userId) {
        LoggerUtil.info("获取用户作物总数，用户ID: {}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            LoggerUtil.warn("获取用户作物总数失败: 用户ID无效");
            throw new IllegalArgumentException("用户ID无效");
        }

        long count = getUserCropList(userId).size();
        LoggerUtil.info("获取用户作物总数成功，用户ID: {}, 作物总数: {}", userId, count);

        return count;
    }

    /**
     * 获取作物总数
     * 获取系统中所有作物的总数
     *
     * @return 作物总数
     */
    public long getCropCount() {
        LoggerUtil.info("获取作物总数");

        long count = cropRepository.count();
        LoggerUtil.info("获取作物总数成功，作物总数: {}", count);

        return count;
    }

    /**
     * 获取指定地区的作物总数
     * 获取指定地区的作物数量
     *
     * @param region 地区
     * @return 作物总数
     * @throws IllegalArgumentException 当地区为空时抛出异常
     */
    public long getRegionCropCount(String region) {
        LoggerUtil.info("获取地区作物总数，地区: {}", region);

        // 验证地区
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取地区作物总数失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        long count = getCropsByRegion(region).size();
        LoggerUtil.info("获取地区作物总数成功，地区: {}, 作物总数: {}", region, count);

        return count;
    }

    /**
     * 验证作物是否属于指定用户
     * 用于权限验证，确保用户只能操作自己的作物
     *
     * @param cropId 作物ID
     * @param userId 用户ID
     * @return 如果作物属于指定用户返回true，否则返回false
     */
    public boolean isCropOwnedByUser(Long cropId, Long userId) {
        LoggerUtil.info("验证作物是否属于指定用户，作物ID: {}, 用户ID: {}", cropId, userId);

        if (cropId == null || cropId <= 0 || userId == null || userId <= 0) {
            LoggerUtil.warn("验证作物所有权失败: 参数无效");
            return false;
        }

        Optional<Crop> cropOptional = cropRepository.findById(cropId);
        if (!cropOptional.isPresent()) {
            LoggerUtil.warn("验证作物所有权失败: 作物不存在，作物ID: {}", cropId);
            return false;
        }

        Crop crop = cropOptional.get();
        boolean isOwned = crop.getUserId().equals(userId);

        if (isOwned) {
            LoggerUtil.info("作物所有权验证成功，作物ID: {}, 用户ID: {}", cropId, userId);
        } else {
            LoggerUtil.warn("作物所有权验证失败: 作物不属于指定用户，作物ID: {}, 用户ID: {}", cropId, userId);
        }

        return isOwned;
    }
}
