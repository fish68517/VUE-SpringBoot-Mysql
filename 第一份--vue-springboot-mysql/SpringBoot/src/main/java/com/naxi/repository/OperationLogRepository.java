package com.naxi.repository;

import com.naxi.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    Page<OperationLog> findByUserId(Long userId, Pageable pageable);

    Page<OperationLog> findByUserIdAndOperationType(Long userId, String operationType, Pageable pageable);
}
