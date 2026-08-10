package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.platform.common.PageResult;
import com.charging.platform.entity.*;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminCrudService {

    private record ModuleDefinition(Class<?> entityType, BaseMapper<?> mapper, String[] keywordColumns) {}

    private final ObjectMapper objectMapper;
    private final Map<String, ModuleDefinition> modules = new LinkedHashMap<>();
    private final ChargingStationMapper stationMapper;
    private final ChargingPileMapper pileMapper;

    public AdminCrudService(ObjectMapper objectMapper,
                            SysUserMapper userMapper,
                            RegionMapper regionMapper,
                            ChargingStationMapper stationMapper,
                            ChargingPileMapper pileMapper,
                            PileRealtimeStatusMapper statusMapper,
                            ChargingUsageRecordMapper usageRecordMapper,
                            RegionStatisticsMapper statisticsMapper,
                            GuidePointMapper guidePointMapper,
                            NoticeMapper noticeMapper,
                            UserFeedbackMapper feedbackMapper) {
        this.objectMapper = objectMapper;
        this.stationMapper = stationMapper;
        this.pileMapper = pileMapper;
        modules.put("users", new ModuleDefinition(SysUser.class, userMapper, new String[]{"username", "nickname", "phone"}));
        modules.put("regions", new ModuleDefinition(Region.class, regionMapper, new String[]{"region_code", "region_name"}));
        modules.put("stations", new ModuleDefinition(ChargingStation.class, stationMapper, new String[]{"station_code", "station_name", "address"}));
        modules.put("piles", new ModuleDefinition(ChargingPile.class, pileMapper, new String[]{"pile_code", "pile_name", "manufacturer"}));
        modules.put("status", new ModuleDefinition(PileRealtimeStatus.class, statusMapper, new String[]{"alarm_code"}));
        modules.put("usage-records", new ModuleDefinition(ChargingUsageRecord.class, usageRecordMapper, new String[]{}));
        modules.put("statistics", new ModuleDefinition(RegionStatistics.class, statisticsMapper, new String[]{}));
        modules.put("guide-points", new ModuleDefinition(GuidePoint.class, guidePointMapper, new String[]{"point_name", "description"}));
        modules.put("notices", new ModuleDefinition(Notice.class, noticeMapper, new String[]{"title", "content"}));
        modules.put("feedback", new ModuleDefinition(UserFeedback.class, feedbackMapper, new String[]{"title", "content", "contact"}));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public PageResult<?> page(String module, long pageNum, long pageSize, String keyword) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 500) {
            throw new BusinessException("分页参数不正确");
        }
        ModuleDefinition definition = definition(module);
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword) && definition.keywordColumns().length > 0) {
            String[] columns = definition.keywordColumns();
            wrapper.like(columns[0], keyword);
            for (int i = 1; i < columns.length; i++) {
                wrapper.or().like(columns[i], keyword);
            }
        }
        wrapper.orderByDesc("id");
        IPage result = ((BaseMapper) definition.mapper()).selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.from(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object get(String module, Long id) {
        Object entity = ((BaseMapper) definition(module).mapper()).selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "数据不存在");
        }
        return entity;
    }

    @Transactional
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object create(String module, JsonNode body) {
        ModuleDefinition definition = definition(module);
        Object entity = objectMapper.convertValue(body, definition.entityType());
        normalize(module, entity, true);
        ((BaseMapper) definition.mapper()).insert(entity);
        return entity;
    }

    @Transactional
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object update(String module, Long id, JsonNode body) {
        ModuleDefinition definition = definition(module);
        if (((BaseMapper) definition.mapper()).selectById(id) == null) {
            throw new BusinessException(404, "数据不存在");
        }
        Object entity = objectMapper.convertValue(body, definition.entityType());
        setId(entity, id);
        normalize(module, entity, false);
        ((BaseMapper) definition.mapper()).updateById(entity);
        return ((BaseMapper) definition.mapper()).selectById(id);
    }

    @Transactional
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void delete(String module, Long id) {
        ModuleDefinition definition = definition(module);
        if (((BaseMapper) definition.mapper()).selectById(id) == null) {
            throw new BusinessException(404, "数据不存在");
        }
        if ("regions".equals(module) && stationMapper.selectCount(
                new QueryWrapper<ChargingStation>().eq("region_id", id)) > 0) {
            throw new BusinessException("该区域下存在充电站，请先处理充电站");
        }
        if ("stations".equals(module) && pileMapper.selectCount(
                new QueryWrapper<ChargingPile>().eq("station_id", id)) > 0) {
            throw new BusinessException("该站点下存在充电桩，请先处理充电桩");
        }
        ((BaseMapper) definition.mapper()).deleteById(id);
    }

    public String[] supportedModules() {
        return modules.keySet().toArray(String[]::new);
    }

    private ModuleDefinition definition(String module) {
        ModuleDefinition definition = modules.get(module);
        if (definition == null) {
            throw new BusinessException(404, "不支持的管理模块");
        }
        return definition;
    }

    private void setId(Object entity, Long id) {
        try {
            Method method = entity.getClass().getMethod("setId", Long.class);
            method.invoke(entity, id);
        } catch (ReflectiveOperationException exception) {
            throw new BusinessException("数据主键设置失败");
        }
    }

    private void normalize(String module, Object entity, boolean creating) {
        LocalDateTime now = LocalDateTime.now();
        if (entity instanceof Notice notice && notice.getPublishStatus() != null
                && notice.getPublishStatus() == 1 && notice.getPublishTime() == null) {
            notice.setPublishTime(now);
        }
        if (entity instanceof UserFeedback feedback && feedback.getProcessStatus() != null
                && feedback.getProcessStatus() == 2 && feedback.getProcessTime() == null) {
            feedback.setProcessTime(now);
        }
        if (entity instanceof PileRealtimeStatus status) {
            if (status.getStatusTime() == null) status.setStatusTime(now);
            if (status.getSourceType() == null) status.setSourceType(0);
        }
        if (creating && entity instanceof SysUser user) {
            if (!StringUtils.hasText(user.getPassword())) user.setPassword("123456");
            if (user.getUserType() == null) user.setUserType(0);
            if (user.getStatus() == null) user.setStatus(1);
        }
        if (creating && entity instanceof Notice notice && notice.getCreateBy() == null) {
            notice.setCreateBy(1L);
        }
    }
}
