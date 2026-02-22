package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.WeatherData;
import com.agricultural.service.WeatherService;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 气象数据控制层
 * 
 * 处理气象数据相关的HTTP请求，包括获取当前气象数据、气象预报、历史气象数据等操作
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * 获取当前气象数据接口
     * 
     * 获取指定地区最新的气象数据
     * 
     * @param region 地区（必填）
     * @return 最新的气象数据
     */
    @GetMapping("/current")
    public Result<WeatherData> getCurrentWeather(
            @RequestParam @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到获取当前气象数据请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("获取当前气象数据请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层获取当前气象数据
            WeatherData currentWeather = weatherService.getCurrentWeather(region);
            
            LoggerUtil.info("获取当前气象数据成功，地区: {}, 数据ID: {}", region, currentWeather.getId());
            return Result.success(currentWeather);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取当前气象数据失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取当前气象数据异常: " + e.getMessage(), e);
            return Result.error("获取当前气象数据失败，请稍后重试");
        }
    }

    /**
     * 获取气象预报接口
     * 
     * 获取指定地区未来一段时间的气象预报数据
     * 
     * @param region 地区（必填）
     * @param forecastHours 预报小时数（可选，默认24小时）
     * @return 气象预报数据列表
     */
    @GetMapping("/forecast")
    public Result<List<WeatherData>> getWeatherForecast(
            @RequestParam @NotBlank(message = "地区不能为空") String region,
            @RequestParam(required = false, defaultValue = "24") Integer forecastHours) {
        
        LoggerUtil.info("收到获取气象预报请求，地区: {}, 预报小时数: {}", region, forecastHours);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("获取气象预报请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            if (forecastHours == null || forecastHours <= 0) {
                LoggerUtil.warn("获取气象预报请求参数验证失败: 预报小时数无效，预报小时数: {}", forecastHours);
                return Result.validationError("预报小时数必须大于0");
            }
            
            // 调用业务层获取气象预报
            List<WeatherData> forecastData = weatherService.getWeatherForecast(region, forecastHours);
            
            LoggerUtil.info("获取气象预报成功，地区: {}, 预报小时数: {}, 数据条数: {}", region, forecastHours, forecastData.size());
            return Result.success(forecastData);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取气象预报失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取气象预报异常: " + e.getMessage(), e);
            return Result.error("获取气象预报失败，请稍后重试");
        }
    }

    /**
     * 获取历史气象数据接口
     * 
     * 获取指定地区和时间范围内的历史气象数据
     * 
     * @param region 地区（必填）
     * @param startTime 开始时间（必填，格式: yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（必填，格式: yyyy-MM-dd HH:mm:ss）
     * @return 历史气象数据列表
     */
    @GetMapping("/history")
    public Result<List<WeatherData>> getHistoricalWeather(
            @RequestParam @NotBlank(message = "地区不能为空") String region,
            @RequestParam @NotNull(message = "开始时间不能为空") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @NotNull(message = "结束时间不能为空") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        LoggerUtil.info("收到获取历史气象数据请求，地区: {}, 开始时间: {}, 结束时间: {}", region, startTime, endTime);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("获取历史气象数据请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            if (startTime == null) {
                LoggerUtil.warn("获取历史气象数据请求参数验证失败: 开始时间为空");
                return Result.validationError("开始时间不能为空");
            }
            
            if (endTime == null) {
                LoggerUtil.warn("获取历史气象数据请求参数验证失败: 结束时间为空");
                return Result.validationError("结束时间不能为空");
            }
            
            if (startTime.isAfter(endTime)) {
                LoggerUtil.warn("获取历史气象数据请求参数验证失败: 开始时间晚于结束时间，开始时间: {}, 结束时间: {}", startTime, endTime);
                return Result.validationError("开始时间不能晚于结束时间");
            }
            
            // 调用业务层获取历史气象数据
            List<WeatherData> historicalData = weatherService.getHistoricalWeather(region, startTime, endTime);
            
            LoggerUtil.info("获取历史气象数据成功，地区: {}, 数据条数: {}", region, historicalData.size());
            return Result.success(historicalData);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取历史气象数据失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取历史气象数据异常: " + e.getMessage(), e);
            return Result.error("获取历史气象数据失败，请稍后重试");
        }
    }

    /**
     * 按地区和时间范围查询气象数据接口
     * 
     * 这是一个通用的查询接口，支持按地区和时间范围查询气象数据
     * 
     * @param region 地区（必填）
     * @param startTime 开始时间（可选，格式: yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（可选，格式: yyyy-MM-dd HH:mm:ss）
     * @return 查询结果的气象数据列表
     */
    @GetMapping("/query")
    public Result<List<WeatherData>> queryWeatherData(
            @RequestParam @NotBlank(message = "地区不能为空") String region,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        LoggerUtil.info("收到查询气象数据请求，地区: {}, 开始时间: {}, 结束时间: {}", region, startTime, endTime);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("查询气象数据请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 如果提供了时间范围，则使用时间范围查询
            if (startTime != null && endTime != null) {
                if (startTime.isAfter(endTime)) {
                    LoggerUtil.warn("查询气象数据请求参数验证失败: 开始时间晚于结束时间，开始时间: {}, 结束时间: {}", startTime, endTime);
                    return Result.validationError("开始时间不能晚于结束时间");
                }
                
                List<WeatherData> weatherDataList = weatherService.getHistoricalWeather(region, startTime, endTime);
                LoggerUtil.info("按时间范围查询气象数据成功，地区: {}, 数据条数: {}", region, weatherDataList.size());
                return Result.success(weatherDataList);
            }
            
            // 否则查询该地区的所有气象数据
            List<WeatherData> weatherDataList = weatherService.getWeatherByRegion(region);
            LoggerUtil.info("按地区查询气象数据成功，地区: {}, 数据条数: {}", region, weatherDataList.size());
            return Result.success(weatherDataList);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("查询气象数据失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("查询气象数据异常: " + e.getMessage(), e);
            return Result.error("查询气象数据失败，请稍后重试");
        }
    }

    /**
     * 获取指定地区的所有气象数据接口
     * 
     * @param region 地区（必填）
     * @return 该地区的所有气象数据列表
     */
    @GetMapping("/region/{region}")
    public Result<List<WeatherData>> getWeatherByRegion(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到按地区获取气象数据请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("按地区获取气象数据请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层获取该地区的所有气象数据
            List<WeatherData> weatherDataList = weatherService.getWeatherByRegion(region);
            
            LoggerUtil.info("按地区获取气象数据成功，地区: {}, 数据条数: {}", region, weatherDataList.size());
            return Result.success(weatherDataList);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按地区获取气象数据失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按地区获取气象数据异常: " + e.getMessage(), e);
            return Result.error("按地区获取气象数据失败，请稍后重试");
        }
    }

    /**
     * 获取指定ID的气象数据接口
     * 
     * @param id 气象数据ID（必填）
     * @return 气象数据详情
     */
    @GetMapping("/{id}")
    public Result<WeatherData> getWeatherDataById(
            @PathVariable @NotNull(message = "气象数据ID不能为空") Long id) {
        
        LoggerUtil.info("收到获取气象数据详情请求，数据ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("获取气象数据详情请求参数验证失败: 数据ID无效");
                return Result.validationError("数据ID无效");
            }
            
            // 调用业务层获取气象数据
            WeatherData weatherData = weatherService.getWeatherDataById(id);
            
            LoggerUtil.info("获取气象数据详情成功，数据ID: {}", id);
            return Result.success(weatherData);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取气象数据详情失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取气象数据详情异常: " + e.getMessage(), e);
            return Result.error("获取气象数据详情失败，请稍后重试");
        }
    }

    /**
     * 获取所有气象数据接口
     * 
     * @return 所有气象数据列表
     */
    @GetMapping("/all")
    public Result<List<WeatherData>> getAllWeatherData() {
        
        LoggerUtil.info("收到获取所有气象数据请求");
        
        try {
            // 调用业务层获取所有气象数据
            List<WeatherData> weatherDataList = weatherService.getAllWeatherData();
            
            LoggerUtil.info("获取所有气象数据成功，数据总数: {}", weatherDataList.size());
            return Result.success(weatherDataList);
            
        } catch (Exception e) {
            LoggerUtil.error("获取所有气象数据异常: " + e.getMessage(), e);
            return Result.error("获取所有气象数据失败，请稍后重试");
        }
    }

    /**
     * 检查指定地区是否有气象数据接口
     * 
     * @param region 地区（必填）
     * @return 是否存在气象数据
     */
    @GetMapping("/check/{region}")
    public Result<Boolean> hasWeatherData(
            @PathVariable @NotBlank(message = "地区不能为空") String region) {
        
        LoggerUtil.info("收到检查地区气象数据是否存在请求，地区: {}", region);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(region)) {
                LoggerUtil.warn("检查地区气象数据请求参数验证失败: 地区为空");
                return Result.validationError("地区不能为空");
            }
            
            // 调用业务层检查是否有气象数据
            boolean hasData = weatherService.hasWeatherData(region);
            
            LoggerUtil.info("检查地区气象数据完成，地区: {}, 是否有数据: {}", region, hasData);
            return Result.success(hasData);
            
        } catch (Exception e) {
            LoggerUtil.error("检查地区气象数据异常: " + e.getMessage(), e);
            return Result.error("检查地区气象数据失败，请稍后重试");
        }
    }
}
