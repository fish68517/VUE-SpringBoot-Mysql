// 1. CheckinTaskRepository.java
package com.shenyang.musicfestival.repository;
import com.shenyang.musicfestival.entity.CheckinTask;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CheckinTaskRepository extends JpaRepository<CheckinTask, Long> {}