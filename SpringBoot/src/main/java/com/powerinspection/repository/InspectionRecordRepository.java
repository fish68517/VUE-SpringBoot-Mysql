package com.powerinspection.repository;

import com.powerinspection.entity.InspectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Long> {
    List<InspectionRecord> findByInspectorId(Long inspectorId);
    boolean existsByTaskId(Long taskId);
}
