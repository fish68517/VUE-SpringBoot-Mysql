package com.agricultural.service;

import com.agricultural.entity.WeatherData;
import com.agricultural.repository.WeatherDataRepository;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 气象数据业务逻辑层
 * 处理气象数据相关的业务逻辑，包括获取当前气象数据、气象预报、历史气象数据等操作
 */
@Service
@Transactional
public class WeatherService {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    /**
     * 获取当前气象数据
     * 获取指定地区最新的气象数据
     *
     * @param region 地区
     * @return 最新的气象数据对象
     * @throws IllegalArgumentException 当地区为空或数据不存在时抛出异常
     */
    public WeatherData getCurrentWeather(String region) {
        LoggerUtil.info("获取当前气象数据，地区: {}", region);

        // 验证地区参数
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取当前气象数据失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 查询最新的气象数据
        WeatherData weatherData = weatherDataRepository.findLatestByRegion(region);
        if (weatherData == null) {
            LoggerUtil.warn("获取当前气象数据失败: 该地区暂无气象数据，地区: {}", region);
            throw new IllegalArgumentException("该地区暂无气象数据");
        }

        LoggerUtil.info("获取当前气象数据成功，地区: {}, 数据ID: {}", region, weatherData.getId());

        return weatherData;
    }

    /**
     * 获取气象预报
     * 获取指定地区未来一段时间的气象预报数据
     * 预报数据是指recordedAt时间在当前时间之后的数据
     *
     * @param region 地区
     * @param forecastHours 预报小时数（默认查询24小时内的数据）
     * @return 气象预报数据列表
     * @throws IllegalArgumentException 当地区为空或预报小时数无效时抛出异常
     */
    public List<WeatherData> getWeatherForecast(String region, Integer forecastHours) {
        LoggerUtil.info("获取气象预报，地区: {}, 预报小时数: {}", region, forecastHours);

        // 验证地区参数
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取气象预报失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 验证预报小时数
        if (forecastHours == null || forecastHours <= 0) {
            LoggerUtil.warn("获取气象预报失败: 预报小时数无效，预报小时数: {}", forecastHours);
            throw new IllegalArgumentException("预报小时数必须大于0");
        }

        // 设置时间范围
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(forecastHours);

        // 查询预报数据
        List<WeatherData> forecastData = weatherDataRepository.findByRegionAndTimeRange(region, startTime, endTime);

        LoggerUtil.info("获取气象预报成功，地区: {}, 预报小时数: {}, 数据条数: {}", region, forecastHours, forecastData.size());

        return forecastData;
    }

    /**
     * 获取历史气象数据
     * 获取指定地区和时间范围内的历史气象数据
     *
     * @param region 地区
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 历史气象数据列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<WeatherData> getHistoricalWeather(String region, LocalDateTime startTime, LocalDateTime endTime) {
        LoggerUtil.info("获取历史气象数据，地区: {}, 开始时间: {}, 结束时间: {}", region, startTime, endTime);

        // 验证地区参数
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("获取历史气象数据失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 验证时间参数
        if (startTime == null || endTime == null) {
            LoggerUtil.warn("获取历史气象数据失败: 时间参数为空");
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }

        // 验证时间范围
        if (startTime.isAfter(endTime)) {
            LoggerUtil.warn("获取历史气象数据失败: 开始时间晚于结束时间，开始时间: {}, 结束时间: {}", startTime, endTime);
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }

        // 查询历史数据
        List<WeatherData> historicalData = weatherDataRepository.findByRegionAndTimeRange(region, startTime, endTime);

        LoggerUtil.info("获取历史气象数据成功，地区: {}, 数据条数: {}", region, historicalData.size());

        return historicalData;
    }

    /**
     * 存储气象数据
     * 将新的气象数据保存到数据库
     *
     * @param region 地区
     * @param temperature 温度
     * @param humidity 湿度
     * @param precipitation 降水量
     * @param windSpeed 风速
     * @param weatherCondition 天气状况
     * @return 保存的气象数据对象
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public WeatherData saveWeatherData(String region, BigDecimal temperature, Integer humidity,
                                       BigDecimal precipitation, BigDecimal windSpeed, String weatherCondition) {
        LoggerUtil.info("保存气象数据，地区: {}, 温度: {}, 湿度: {}, 降水量: {}, 风速: {}, 天气状况: {}",
                region, temperature, humidity, precipitation, windSpeed, weatherCondition);

        // 验证地区参数
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("保存气象数据失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 验证温度
        if (temperature != null) {
            if (temperature.compareTo(new BigDecimal("-50")) < 0 || temperature.compareTo(new BigDecimal("60")) > 0) {
                LoggerUtil.warn("保存气象数据失败: 温度值超出合理范围，温度: {}", temperature);
                throw new IllegalArgumentException("温度值必须在-50°C到60°C之间");
            }
        }

        // 验证湿度
        if (humidity != null) {
            if (humidity < 0 || humidity > 100) {
                LoggerUtil.warn("保存气象数据失败: 湿度值超出合理范围，湿度: {}", humidity);
                throw new IllegalArgumentException("湿度值必须在0-100之间");
            }
        }

        // 验证降水量
        if (precipitation != null) {
            if (precipitation.compareTo(BigDecimal.ZERO) < 0) {
                LoggerUtil.warn("保存气象数据失败: 降水量不能为负数，降水量: {}", precipitation);
                throw new IllegalArgumentException("降水量不能为负数");
            }
        }

        // 验证风速
        if (windSpeed != null) {
            if (windSpeed.compareTo(BigDecimal.ZERO) < 0) {
                LoggerUtil.warn("保存气象数据失败: 风速不能为负数，风速: {}", windSpeed);
                throw new IllegalArgumentException("风速不能为负数");
            }
        }

        // 创建气象数据对象
        WeatherData weatherData = WeatherData.builder()
                .region(region)
                .temperature(temperature)
                .humidity(humidity)
                .precipitation(precipitation)
                .windSpeed(windSpeed)
                .weatherCondition(weatherCondition)
                .recordedAt(LocalDateTime.now())
                .build();

        // 保存数据
        WeatherData savedData = weatherDataRepository.save(weatherData);
        LoggerUtil.info("气象数据保存成功，地区: {}, 数据ID: {}", region, savedData.getId());

        return savedData;
    }

    /**
     * 更新气象数据
     * 更新指定ID的气象数据
     *
     * @param weatherDataId 气象数据ID
     * @param temperature 温度
     * @param humidity 湿度
     * @param precipitation 降水量
     * @param windSpeed 风速
     * @param weatherCondition 天气状况
     * @return 更新后的气象数据对象
     * @throws IllegalArgumentException 当数据不存在或参数无效时抛出异常
     */
    public WeatherData updateWeatherData(Long weatherDataId, BigDecimal temperature, Integer humidity,
                                         BigDecimal precipitation, BigDecimal windSpeed, String weatherCondition) {
        LoggerUtil.info("更新气象数据，数据ID: {}", weatherDataId);

        // 验证ID
        if (weatherDataId == null || weatherDataId <= 0) {
            LoggerUtil.warn("更新气象数据失败: 数据ID无效");
            throw new IllegalArgumentException("数据ID无效");
        }

        // 查询现有数据
        Optional<WeatherData> existingData = weatherDataRepository.findById(weatherDataId);
        if (!existingData.isPresent()) {
            LoggerUtil.warn("更新气象数据失败: 数据不存在，数据ID: {}", weatherDataId);
            throw new IllegalArgumentException("气象数据不存在");
        }

        WeatherData weatherData = existingData.get();

        // 验证并更新温度
        if (temperature != null) {
            if (temperature.compareTo(new BigDecimal("-50")) < 0 || temperature.compareTo(new BigDecimal("60")) > 0) {
                LoggerUtil.warn("更新气象数据失败: 温度值超出合理范围，温度: {}", temperature);
                throw new IllegalArgumentException("温度值必须在-50°C到60°C之间");
            }
            weatherData.setTemperature(temperature);
        }

        // 验证并更新湿度
        if (humidity != null) {
            if (humidity < 0 || humidity > 100) {
                LoggerUtil.warn("更新气象数据失败: 湿度值超出合理范围，湿度: {}", humidity);
                throw new IllegalArgumentException("湿度值必须在0-100之间");
            }
            weatherData.setHumidity(humidity);
        }

        // 验证并更新降水量
        if (precipitation != null) {
            if (precipitation.compareTo(BigDecimal.ZERO) < 0) {
                LoggerUtil.warn("更新气象数据失败: 降水量不能为负数，降水量: {}", precipitation);
                throw new IllegalArgumentException("降水量不能为负数");
            }
            weatherData.setPrecipitation(precipitation);
        }

        // 验证并更新风速
        if (windSpeed != null) {
            if (windSpeed.compareTo(BigDecimal.ZERO) < 0) {
                LoggerUtil.warn("更新气象数据失败: 风速不能为负数，风速: {}", windSpeed);
                throw new IllegalArgumentException("风速不能为负数");
            }
            weatherData.setWindSpeed(windSpeed);
        }

        // 更新天气状况
        if (StringUtil.isNotBlank(weatherCondition)) {
            weatherData.setWeatherCondition(weatherCondition);
        }

        // 保存更新
        WeatherData updatedData = weatherDataRepository.save(weatherData);
        LoggerUtil.info("气象数据更新成功，数据ID: {}", weatherDataId);

        return updatedData;
    }

    /**
     * 根据地区查询气象数据列表
     * 获取指定地区的所有气象数据
     *
     * @param region 地区
     * @return 气象数据列表
     * @throws IllegalArgumentException 当地区为空时抛出异常
     */
    public List<WeatherData> getWeatherByRegion(String region) {
        LoggerUtil.info("根据地区查询气象数据，地区: {}", region);

        // 验证地区参数
        if (StringUtil.isBlank(region)) {
            LoggerUtil.warn("查询气象数据失败: 地区为空");
            throw new IllegalArgumentException("地区不能为空");
        }

        // 查询数据
        List<WeatherData> weatherDataList = weatherDataRepository.findByRegion(region);
        LoggerUtil.info("根据地区查询气象数据成功，地区: {}, 数据条数: {}", region, weatherDataList.size());

        return weatherDataList;
    }

    /**
     * 根据ID查询气象数据
     *
     * @param weatherDataId 气象数据ID
     * @return 气象数据对象
     * @throws IllegalArgumentException 当数据不存在时抛出异常
     */
    public WeatherData getWeatherDataById(Long weatherDataId) {
        LoggerUtil.info("根据ID查询气象数据，数据ID: {}", weatherDataId);

        // 验证ID
        if (weatherDataId == null || weatherDataId <= 0) {
            LoggerUtil.warn("查询气象数据失败: 数据ID无效");
            throw new IllegalArgumentException("数据ID无效");
        }

        // 查询数据
        Optional<WeatherData> weatherData = weatherDataRepository.findById(weatherDataId);
        if (!weatherData.isPresent()) {
            LoggerUtil.warn("查询气象数据失败: 数据不存在，数据ID: {}", weatherDataId);
            throw new IllegalArgumentException("气象数据不存在");
        }

        LoggerUtil.info("根据ID查询气象数据成功，数据ID: {}", weatherDataId);

        return weatherData.get();
    }

    /**
     * 删除气象数据
     *
     * @param weatherDataId 气象数据ID
     * @throws IllegalArgumentException 当数据不存在时抛出异常
     */
    public void deleteWeatherData(Long weatherDataId) {
        LoggerUtil.info("删除气象数据，数据ID: {}", weatherDataId);

        // 验证数据是否存在
        WeatherData weatherData = getWeatherDataById(weatherDataId);

        // 删除数据
        weatherDataRepository.deleteById(weatherDataId);
        LoggerUtil.info("气象数据删除成功，数据ID: {}", weatherDataId);
    }

    /**
     * 获取所有气象数据
     *
     * @return 气象数据列表
     */
    public List<WeatherData> getAllWeatherData() {
        LoggerUtil.info("获取所有气象数据");

        List<WeatherData> weatherDataList = weatherDataRepository.findAll();
        LoggerUtil.info("获取所有气象数据成功，数据总数: {}", weatherDataList.size());

        return weatherDataList;
    }

    /**
     * 检查指定地区是否有气象数据
     *
     * @param region 地区
     * @return 如果存在数据返回true，否则返回false
     */
    public boolean hasWeatherData(String region) {
        LoggerUtil.info("检查地区是否有气象数据，地区: {}", region);

        if (StringUtil.isBlank(region)) {
            return false;
        }

        List<WeatherData> weatherDataList = weatherDataRepository.findByRegion(region);
        boolean hasData = !weatherDataList.isEmpty();

        LoggerUtil.info("地区气象数据检查完成，地区: {}, 是否有数据: {}", region, hasData);

        return hasData;
    }
}
