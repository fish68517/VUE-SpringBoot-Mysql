package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.FestivalDTO;
import com.shenyang.musicfestival.dto.ScheduleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Festival operations
 */
public interface FestivalService {

    /**
     * Get festival information by ID
     */
    Optional<FestivalDTO> getFestivalById(Long id);

    /**
     * Get the current ongoing festival
     */
    Optional<FestivalDTO> getCurrentFestival();

    /**
     * Get all schedules for a festival
     */
    List<ScheduleDTO> getFestivalSchedule(Long festivalId);

}
