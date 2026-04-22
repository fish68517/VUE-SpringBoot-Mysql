package com.powerinspection.repository;

import com.powerinspection.entity.InspectionTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionTaskRepository extends JpaRepository<InspectionTask, Long> {
    List<InspectionTask> findByInspectorId(Long inspectorId);
}
