package com.powerinspection.repository;

import com.powerinspection.entity.DeviceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceCategoryRepository extends JpaRepository<DeviceCategory, Long> {
}
