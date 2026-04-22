package com.powerinspection.repository;

import com.powerinspection.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    List<RepairOrder> findByMaintainerId(Long maintainerId);
    boolean existsByDefectId(Long defectId);
    Optional<RepairOrder> findByDefectId(Long defectId);
}
