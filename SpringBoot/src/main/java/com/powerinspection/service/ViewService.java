package com.powerinspection.service;

import com.powerinspection.entity.*;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ViewService {

    public Map<String, Object> userMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("realName", user.getRealName());
        map.put("role", user.getRole());
        map.put("phone", user.getPhone());
        map.put("email", user.getEmail());
        map.put("status", user.getStatus());
        map.put("createdAt", user.getCreatedAt());
        return map;
    }

    public Map<String, Object> categoryMap(DeviceCategory category) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", category.getId());
        map.put("code", category.getCode());
        map.put("name", category.getName());
        map.put("description", category.getDescription());
        return map;
    }

    public Map<String, Object> deviceMap(Device device, String imageBaseUrl) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", device.getId());
        map.put("categoryId", device.getCategory().getId());
        map.put("categoryName", device.getCategory().getName());
        map.put("deviceCode", device.getDeviceCode());
        map.put("deviceName", device.getDeviceName());
        map.put("location", device.getLocation());
        map.put("status", device.getStatus());
        map.put("lastInspectionTime", device.getLastInspectionTime());
        map.put("imageName", device.getImageName());
        map.put("imageUrl", device.getImageName() == null ? null : imageBaseUrl + "/" + device.getImageName());
        map.put("remark", device.getRemark());
        return map;
    }

    public Map<String, Object> taskMap(InspectionTask task) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", task.getId());
        map.put("taskNo", task.getTaskNo());
        map.put("title", task.getTitle());
        map.put("deviceId", task.getDevice().getId());
        map.put("deviceName", task.getDevice().getDeviceName());
        map.put("deviceCode", task.getDevice().getDeviceCode());
        map.put("inspectorId", task.getInspector().getId());
        map.put("inspectorName", task.getInspector().getRealName());
        map.put("plannedDate", task.getPlannedDate());
        map.put("status", task.getStatus());
        map.put("priority", task.getPriority());
        map.put("remark", task.getRemark());
        return map;
    }

    public Map<String, Object> taskItemMap(InspectionTaskItem item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", item.getId());
        map.put("taskId", item.getTask().getId());
        map.put("standardId", item.getStandard() == null ? null : item.getStandard().getId());
        map.put("itemName", item.getItemName());
        map.put("standardValue", item.getStandardValue());
        map.put("checkMethod", item.getCheckMethod());
        map.put("abnormalRule", item.getAbnormalRule());
        map.put("sortOrder", item.getSortOrder());
        map.put("checkedValue", item.getCheckedValue());
        map.put("itemResult", item.getItemResult());
        map.put("itemRemark", item.getItemRemark());
        return map;
    }

    public Map<String, Object> recordMap(InspectionRecord record, String imageBaseUrl) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", record.getId());
        map.put("taskId", record.getTask().getId());
        map.put("taskNo", record.getTask().getTaskNo());
        map.put("deviceName", record.getDevice().getDeviceName());
        map.put("inspectorName", record.getInspector().getRealName());
        map.put("result", record.getResult());
        map.put("statusDescription", record.getStatusDescription());
        map.put("imageName", record.getImageName());
        map.put("imageUrl", record.getImageName() == null ? null : imageBaseUrl + "/" + record.getImageName());
        map.put("inspectionTime", record.getInspectionTime());
        map.put("needRepair", record.getNeedRepair());
        return map;
    }

    public Map<String, Object> defectMap(Defect defect, String imageBaseUrl) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", defect.getId());
        map.put("recordId", defect.getRecord().getId());
        map.put("deviceId", defect.getDevice().getId());
        map.put("deviceName", defect.getDevice().getDeviceName());
        map.put("reporterId", defect.getReporter().getId());
        map.put("reporterName", defect.getReporter().getRealName());
        map.put("level", defect.getLevel());
        map.put("status", defect.getStatus());
        map.put("description", defect.getDescription());
        map.put("imageName", defect.getImageName());
        map.put("imageUrl", defect.getImageName() == null ? null : imageBaseUrl + "/" + defect.getImageName());
        map.put("reportedAt", defect.getReportedAt());
        return map;
    }

    public Map<String, Object> repairOrderMap(RepairOrder order, String imageBaseUrl) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("defectId", order.getDefect().getId());
        map.put("deviceName", order.getDevice().getDeviceName());
        map.put("maintainerId", order.getMaintainer().getId());
        map.put("maintainerName", order.getMaintainer().getRealName());
        map.put("status", order.getStatus());
        map.put("measures", order.getMeasures());
        map.put("result", order.getResult());
        map.put("imageName", order.getImageName());
        map.put("imageUrl", order.getImageName() == null ? null : imageBaseUrl + "/" + order.getImageName());
        map.put("remark", order.getRemark());
        map.put("finishedAt", order.getFinishedAt());
        return map;
    }

    public Map<String, Object> standardMap(InspectionStandard standard) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", standard.getId());
        map.put("categoryId", standard.getCategory().getId());
        map.put("categoryName", standard.getCategory().getName());
        map.put("itemName", standard.getItemName());
        map.put("standardValue", standard.getStandardValue());
        map.put("checkMethod", standard.getCheckMethod());
        map.put("cycleDays", standard.getCycleDays());
        map.put("abnormalRule", standard.getAbnormalRule());
        map.put("remark", standard.getRemark());
        return map;
    }

    public Map<String, Object> noticeMap(Notice notice) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", notice.getId());
        map.put("title", notice.getTitle());
        map.put("content", notice.getContent());
        map.put("pinned", notice.getPinned());
        map.put("status", notice.getStatus());
        map.put("publishTime", notice.getPublishTime());
        map.put("publisherName", notice.getPublisher().getRealName());
        return map;
    }

    public Map<String, Object> operationLogMap(OperationLog log) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", log.getId());
        map.put("module", log.getModule());
        map.put("action", log.getAction());
        map.put("username", log.getUsername());
        map.put("realName", log.getRealName());
        map.put("role", log.getRole());
        map.put("requestMethod", log.getRequestMethod());
        map.put("requestPath", log.getRequestPath());
        map.put("ip", log.getIp());
        map.put("success", log.getSuccess());
        map.put("message", log.getMessage());
        map.put("details", log.getDetails());
        map.put("createdAt", log.getCreatedAt());
        return map;
    }
}
