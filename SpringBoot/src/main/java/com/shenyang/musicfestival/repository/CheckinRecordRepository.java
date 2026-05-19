// 2. CheckinRecordRepository.java
package com.shenyang.musicfestival.repository;
import com.shenyang.musicfestival.entity.CheckinRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckinRecordRepository extends JpaRepository<CheckinRecord, Long> {
    boolean existsByUserIdAndTaskId(Long userId, Long taskId);
    List<CheckinRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}
