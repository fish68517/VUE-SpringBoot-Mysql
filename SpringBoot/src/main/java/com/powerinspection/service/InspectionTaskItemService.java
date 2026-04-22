package com.powerinspection.service;

import com.powerinspection.entity.InspectionStandard;
import com.powerinspection.entity.InspectionTask;
import com.powerinspection.entity.InspectionTaskItem;
import com.powerinspection.repository.InspectionStandardRepository;
import com.powerinspection.repository.InspectionTaskItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionTaskItemService {

    private final InspectionStandardRepository inspectionStandardRepository;
    private final InspectionTaskItemRepository inspectionTaskItemRepository;

    public List<InspectionTaskItem> rebuildTaskItems(InspectionTask task) {
        inspectionTaskItemRepository.deleteByTaskId(task.getId());
        List<InspectionStandard> standards = inspectionStandardRepository.findAll().stream()
                .filter(item -> item.getCategory().getId().equals(task.getDevice().getCategory().getId()))
                .toList();

        List<InspectionTaskItem> items = new ArrayList<>();
        int order = 1;
        for (InspectionStandard standard : standards) {
            InspectionTaskItem item = new InspectionTaskItem();
            item.setTask(task);
            item.setStandard(standard);
            item.setItemName(standard.getItemName());
            item.setStandardValue(standard.getStandardValue());
            item.setCheckMethod(standard.getCheckMethod());
            item.setAbnormalRule(standard.getAbnormalRule());
            item.setSortOrder(order++);
            items.add(item);
        }
        return inspectionTaskItemRepository.saveAll(items);
    }
}
