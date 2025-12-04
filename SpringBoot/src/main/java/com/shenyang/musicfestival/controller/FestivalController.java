package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.FestivalDTO;
import com.shenyang.musicfestival.dto.ScheduleDTO;
import com.shenyang.musicfestival.service.FestivalService;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Festival operations
 */
@RestController
@RequestMapping("/festival")
@RequiredArgsConstructor
public class FestivalController {

    private final FestivalService festivalService;

    /**
     * Get festival basic information
     * GET /festival/info
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<FestivalDTO>> getFestivalInfo() {
        var festival = festivalService.getCurrentFestival();
        if (festival.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("音乐节信息不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(festival.get(), "获取音乐节信息成功"));
    }

    /**
     * Get festival performance schedule
     * GET /festival/schedule
     */
    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getFestivalSchedule() {
        var festival = festivalService.getCurrentFestival();
        if (festival.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("音乐节信息不存在"));
        }

        List<ScheduleDTO> schedules = festivalService.getFestivalSchedule(festival.get().getId());
        return ResponseEntity.ok(ApiResponse.success(schedules, "获取演出日程成功"));
    }

}
