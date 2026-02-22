package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.Crop;
import com.agricultural.service.CropService;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 作物控制层
 * 
 * 处理作物相关的HTTP请求，包括添加、查询、更新、删除等操作
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/crops")
public class CropController {

    @Autowired
    private CropService cropService;

    /**
     * 添加作物接口
     * 
     * 创建新的作物记录
     * 
     * @param cropName 作物名称（必填）
     * @param region 地区（必填）
     * @param userId 用户ID（必填）
     * @param growthStage 生育期（可选）
     * @param plantingDate 播种日期（可选，格式: yyyy-MM-dd）
     * @param expectedHarvestDate 预期收获日期（可选，格式: yyyy-MM-dd）
     * @return 创建的作物信息
     */
    @PostMapping
    public Result<Crop> addCrop(
            @RequestParam @NotBlank(message = "作物名称不能为空") String cropName,
            @RequestParam @NotBlank(message = "地区不能为空") String region,
            @RequestParam @NotNull(message = "用户ID不能为空") Long userId,
            @RequestParam(required = false) String growthStage,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate plantingDate,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expectedHarvestDate) {
        
        LoggerUtil.info("收到添加作物请求，作物名称: {}, 用户ID: {}, 地区: {}", cropName, userId, region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(cropName)) {
                LoggerUtil.warn("添加作物请求参数验证失败: 作物名称为空");
                return Result.validationError("作物名称不能为空");
            }
            
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("添加作物请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("添加作物请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层添加作物
            Crop addedCrop = cropService.addCrop(cropName, growthStage, region, userId, 
                    plantingDate, expectedHarvestDate);
            
            LoggerUtil.info("添加作物成功，作物ID: {}, 作物名称: {}", addedCrop.getId(), cropName);
            return Result.success("作物添加成功", addedCrop);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("添加作物失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("添加作物异常: " + e.getMessage(), e);
            return Result.error("添加作物失败，请稍后重试");
        }
    }

    /**
     * 获取作物列表接口
     * 
     * 获取所有作物信息，支持按用户ID、地区等条件筛选
     * 
     * @param userId 用户ID（可选）
     * @param region 地区（可选）
     * @return 作物列表
     */
    @GetMapping
    public Result<List<Crop>> getCropList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String region) {
        
        LoggerUtil.info("收到获取作物列表请求，用户ID: {}, 地区: {}", userId, region);
        
        try {
            List<Crop> crops;
            
            // 根据条件查询作物
            if (userId != null && userId > 0 && StringUtil.isNotBlank(region)) {
                // 按用户ID和地区查询
                crops = cropService.getCropsByUserAndRegion(userId, region);
            } else if (userId != null && userId > 0) {
                // 按用户ID查询
                crops = cropService.getUserCropList(userId);
            } else if (StringUtil.isNotBlank(region)) {
                // 按地区查询
                crops = cropService.getCropsByRegion(region);
            } else {
                // 获取所有作物
                crops = cropService.getAllCrops();
            }
            
            LoggerUtil.info("获取作物列表成功，作物总数: {}", crops.size());
            return Result.success(crops);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取作物列表失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取作物列表异常: " + e.getMessage(), e);
            return Result.error("获取作物列表失败，请稍后重试");
        }
    }

    /**
     * 获取作物详情接口
     * 
     * 根据作物ID获取作物详细信息
     * 
     * @param id 作物ID（必填）
     * @return 作物详情
     */
    @GetMapping("/{id}")
    public Result<Crop> getCropById(
            @PathVariable @NotNull(message = "作物ID不能为空") Long id) {
        
        LoggerUtil.info("收到获取作物详情请求，作物ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("获取作物详情请求参数验证失败: 作物ID无效");
                return Result.validationError("作物ID无效");
            }
            
            // 调用业务层获取作物详情
            Crop crop = cropService.getCropById(id);
            
            LoggerUtil.info("获取作物详情成功，作物ID: {}", id);
            return Result.success(crop);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取作物详情失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取作物详情异常: " + e.getMessage(), e);
            return Result.error("获取作物详情失败，请稍后重试");
        }
    }

    /**
     * 按地区查询作物接口
     * 
     * 获取指定地区的所有作物信息
     * 
     * @param region 地区（必填）
     * @return 该地区的作物列表
     */
    @GetMapping("/region/{region}")
    public Result<List<Crop>> getCropsByRegion(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到按地区查询作物请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("按地区查询作物请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层按地区查询作物
            List<Crop> crops = cropService.getCropsByRegion(region);
            
            LoggerUtil.info("按地区查询作物成功，地区: {}, 作物数: {}", region, crops.size());
            return Result.success(crops);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按地区查询作物失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按地区查询作物异常: " + e.getMessage(), e);
            return Result.error("按地区查询作物失败，请稍后重试");
        }
    }

    /**
     * 按作物名称查询作物接口
     * 
     * 获取指定名称的所有作物信息
     * 
     * @param cropName 作物名称（必填）
     * @return 该名称的作物列表
     */
    @GetMapping("/name/{cropName}")
    public Result<List<Crop>> getCropsByName(
            @PathVariable @NotBlank(message = "作物名称不能为空") String cropName) {
        
        LoggerUtil.info("收到按作物名称查询作物请求，作物名称: {}", cropName);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(cropName)) {
                LoggerUtil.warn("按作物名称查询作物请求参数验证失败: 作物名称为空");
                return Result.validationError("作物名称不能为空");
            }
            
            // 调用业务层按作物名称查询作物
            List<Crop> crops = cropService.getCropsByName(cropName);
            
            LoggerUtil.info("按作物名称查询作物成功，作物名称: {}, 作物数: {}", cropName, crops.size());
            return Result.success(crops);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按作物名称查询作物失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按作物名称查询作物异常: " + e.getMessage(), e);
            return Result.error("按作物名称查询作物失败，请稍后重试");
        }
    }

    /**
     * 按生育期查询作物接口
     * 
     * 获取指定生育期的所有作物信息
     * 
     * @param growthStage 生育期（必填）
     * @return 该生育期的作物列表
     */
    @GetMapping("/growth-stage/{growthStage}")
    public Result<List<Crop>> getCropsByGrowthStage(
            @PathVariable @NotBlank(message = "生育期不能为空") String growthStage) {
        
        LoggerUtil.info("收到按生育期查询作物请求，生育期: {}", growthStage);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(growthStage)) {
                LoggerUtil.warn("按生育期查询作物请求参数验证失败: 生育期为空");
                return Result.validationError("生育期不能为空");
            }
            
            // 调用业务层按生育期查询作物
            List<Crop> crops = cropService.getCropsByGrowthStage(growthStage);
            
            LoggerUtil.info("按生育期查询作物成功，生育期: {}, 作物数: {}", growthStage, crops.size());
            return Result.success(crops);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按生育期查询作物失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按生育期查询作物异常: " + e.getMessage(), e);
            return Result.error("按生育期查询作物失败，请稍后重试");
        }
    }

    /**
     * 按用户ID查询作物接口
     * 
     * 获取指定用户的所有作物信息
     * 
     * @param userId 用户ID（必填）
     * @return 该用户的作物列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Crop>> getCropsByUser(
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        
        LoggerUtil.info("收到按用户ID查询作物请求，用户ID: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("按用户ID查询作物请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层按用户ID查询作物
            List<Crop> crops = cropService.getUserCropList(userId);
            
            LoggerUtil.info("按用户ID查询作物成功，用户ID: {}, 作物数: {}", userId, crops.size());
            return Result.success(crops);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按用户ID查询作物失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按用户ID查询作物异常: " + e.getMessage(), e);
            return Result.error("按用户ID查询作物失败，请稍后重试");
        }
    }

    /**
     * 更新作物接口
     * 
     * 更新作物的信息，包括作物名称、生育期、地区、播种日期、预期收获日期等
     * 
     * @param id 作物ID（必填）
     * @param cropName 作物名称（可选）
     * @param growthStage 生育期（可选）
     * @param region 地区（可选）
     * @param plantingDate 播种日期（可选，格式: yyyy-MM-dd）
     * @param expectedHarvestDate 预期收获日期（可选，格式: yyyy-MM-dd）
     * @return 更新后的作物信息
     */
    @PutMapping("/{id}")
    public Result<Crop> updateCrop(
            @PathVariable @NotNull(message = "作物ID不能为空") Long id,
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String growthStage,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate plantingDate,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expectedHarvestDate) {
        
        LoggerUtil.info("收到更新作物信息请求，作物ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("更新作物信息请求参数验证失败: 作物ID无效");
                return Result.validationError("作物ID无效");
            }
            
            // 检查是否至少提供了一个要更新的字段
            if (StringUtil.isBlank(cropName) && StringUtil.isBlank(growthStage) && 
                StringUtil.isBlank(region) && plantingDate == null && expectedHarvestDate == null) {
                LoggerUtil.warn("更新作物信息请求参数验证失败: 没有提供要更新的字段");
                return Result.validationError("至少需要提供一个要更新的字段");
            }
            
            // 调用业务层更新作物信息
            Crop updatedCrop = cropService.updateCrop(id, cropName, growthStage, region, 
                    plantingDate, expectedHarvestDate);
            
            LoggerUtil.info("更新作物信息成功，作物ID: {}", id);
            return Result.success("作物信息更新成功", updatedCrop);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("更新作物信息失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("更新作物信息异常: " + e.getMessage(), e);
            return Result.error("更新作物信息失败，请稍后重试");
        }
    }

    /**
     * 删除作物接口
     * 
     * 从数据库中删除指定的作物记录
     * 
     * @param id 作物ID（必填）
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCrop(
            @PathVariable @NotNull(message = "作物ID不能为空") Long id) {
        
        LoggerUtil.info("收到删除作物请求，作物ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("删除作物请求参数验证失败: 作物ID无效");
                return Result.validationError("作物ID无效");
            }
            
            // 调用业务层删除作物
            cropService.deleteCrop(id);
            
            LoggerUtil.info("删除作物成功，作物ID: {}", id);
            return Result.success();
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("删除作物失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("删除作物异常: " + e.getMessage(), e);
            return Result.error("删除作物失败，请稍后重试");
        }
    }

    /**
     * 获取作物总数接口
     * 
     * 获取系统中所有作物的总数
     * 
     * @return 作物总数
     */
    @GetMapping("/count")
    public Result<Long> getCropCount() {
        
        LoggerUtil.info("收到获取作物总数请求");
        
        try {
            // 调用业务层获取作物总数
            long count = cropService.getCropCount();
            
            LoggerUtil.info("获取作物总数成功，作物总数: {}", count);
            return Result.success(count);
            
        } catch (Exception e) {
            LoggerUtil.error("获取作物总数异常: " + e.getMessage(), e);
            return Result.error("获取作物总数失败，请稍后重试");
        }
    }

    /**
     * 获取用户作物总数接口
     * 
     * 获取指定用户的作物数量
     * 
     * @param userId 用户ID（必填）
     * @return 用户的作物总数
     */
    @GetMapping("/user/{userId}/count")
    public Result<Long> getUserCropCount(
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        
        LoggerUtil.info("收到获取用户作物总数请求，用户ID: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || userId <= 0) {
                LoggerUtil.warn("获取用户作物总数请求参数验证失败: 用户ID无效");
                return Result.validationError("用户ID无效");
            }
            
            // 调用业务层获取用户作物总数
            long count = cropService.getUserCropCount(userId);
            
            LoggerUtil.info("获取用户作物总数成功，用户ID: {}, 作物总数: {}", userId, count);
            return Result.success(count);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取用户作物总数失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取用户作物总数异常: " + e.getMessage(), e);
            return Result.error("获取用户作物总数失败，请稍后重试");
        }
    }

    /**
     * 获取地区作物总数接口
     * 
     * 获取指定地区的作物数量
     * 
     * @param region 地区（必填）
     * @return 地区的作物总数
     */
    @GetMapping("/region/{region}/count")
    public Result<Long> getRegionCropCount(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到获取地区作物总数请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("获取地区作物总数请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层获取地区作物总数
            long count = cropService.getRegionCropCount(region);
            
            LoggerUtil.info("获取地区作物总数成功，地区: {}, 作物总数: {}", region, count);
            return Result.success(count);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取地区作物总数失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取地区作物总数异常: " + e.getMessage(), e);
            return Result.error("获取地区作物总数失败，请稍后重试");
        }
    }
}
