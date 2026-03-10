// 5. PointsHistoryRepository.java
package com.shenyang.musicfestival.repository;
import com.shenyang.musicfestival.entity.PointsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {
    List<PointsHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}