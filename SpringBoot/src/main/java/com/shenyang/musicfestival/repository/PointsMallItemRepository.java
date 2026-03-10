// 3. PointsMallItemRepository.java
package com.shenyang.musicfestival.repository;
import com.shenyang.musicfestival.entity.PointsMallItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PointsMallItemRepository extends JpaRepository<PointsMallItem, Long> {
    List<PointsMallItem> findByIsActiveTrue();
}