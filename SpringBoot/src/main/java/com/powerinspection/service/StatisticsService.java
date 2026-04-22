package com.powerinspection.service;

import com.powerinspection.entity.*;
import com.powerinspection.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final InspectionTaskRepository taskRepository;
    private final InspectionRecordRepository recordRepository;
    private final DefectRepository defectRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final NoticeRepository noticeRepository;

    public Map<String, Object> buildOverview() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<InspectionTask> tasks = taskRepository.findAll();
        List<InspectionRecord> records = recordRepository.findAll();
        List<Defect> defects = defectRepository.findAll();
        List<RepairOrder> orders = repairOrderRepository.findAll();

        result.put("summary", Map.of(
                "userCount", userRepository.count(),
                "deviceCount", deviceRepository.count(),
                "taskCount", tasks.size(),
                "recordCount", records.size(),
                "defectCount", defects.size(),
                "repairOrderCount", orders.size(),
                "noticeCount", noticeRepository.count()
        ));

        result.put("taskStatusStats", countByName(tasks, task -> task.getStatus().name()));
        result.put("defectLevelStats", countByName(defects, Defect::getLevel));
        result.put("repairStatusStats", countByName(orders, order -> order.getStatus().name()));

        LocalDate startDate = LocalDate.now().minusDays(6);
        List<String> days = startDate.datesUntil(LocalDate.now().plusDays(1)).map(LocalDate::toString).toList();
        result.put("days", days);
        result.put("recordTrend", days.stream()
                .map(day -> records.stream().filter(item -> item.getInspectionTime() != null && item.getInspectionTime().toLocalDate().toString().equals(day)).count())
                .toList());
        result.put("defectTrend", days.stream()
                .map(day -> defects.stream().filter(item -> item.getReportedAt() != null && item.getReportedAt().toLocalDate().toString().equals(day)).count())
                .toList());
        result.put("repairTrend", days.stream()
                .map(day -> orders.stream().filter(item -> item.getFinishedAt() != null && item.getFinishedAt().toLocalDate().toString().equals(day)).count())
                .toList());

        Map<String, Long> deviceDefectCount = defects.stream()
                .collect(Collectors.groupingBy(defect -> defect.getDevice().getDeviceName(), LinkedHashMap::new, Collectors.counting()));
        List<Map<String, Object>> topDevices = deviceDefectCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> Map.<String, Object>of("name", entry.getKey(), "value", entry.getValue()))
                .toList();
        result.put("topDevices", topDevices);

        return result;
    }

    private <T> List<Map<String, Object>> countByName(List<T> source, Function<T, String> classifier) {
        Map<String, Long> map = source.stream()
                .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.counting()));
        return map.entrySet().stream()
                .map(entry -> Map.<String, Object>of("name", entry.getKey(), "value", entry.getValue()))
                .toList();
    }
}
