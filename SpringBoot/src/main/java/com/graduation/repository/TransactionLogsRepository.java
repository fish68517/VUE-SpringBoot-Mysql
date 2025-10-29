package com.graduation.repository;

import com.graduation.entity.TransactionLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogsRepository extends JpaRepository<TransactionLogs, Long> {
    // JpaRepository 已经提供了 save 方法
}