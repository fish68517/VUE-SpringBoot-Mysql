package com.powerinspection.repository;

import com.powerinspection.entity.Defect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DefectRepository extends JpaRepository<Defect, Long> {
    Optional<Defect> findByRecordId(Long recordId);
}
