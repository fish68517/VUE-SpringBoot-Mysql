package com.powerinspection.repository;

import com.powerinspection.entity.InspectionTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionTaskItemRepository extends JpaRepository<InspectionTaskItem, Long> {
    List<InspectionTaskItem> findByTaskIdOrderBySortOrderAscIdAsc(Long taskId);
    void deleteByTaskId(Long taskId);
}
