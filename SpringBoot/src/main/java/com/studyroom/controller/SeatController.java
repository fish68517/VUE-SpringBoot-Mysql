package com.studyroom.controller;

import com.studyroom.dto.Result;
import com.studyroom.entity.Seat;
import com.studyroom.service.SeatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seat")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/list")
    public Result<List<Seat>> listSeats() {
        return seatService.listSeats();
    }

    @GetMapping("/area/{area}")
    public Result<List<Seat>> listSeatsByArea(@PathVariable String area) {
        return seatService.listSeatsByArea(area);
    }

    @GetMapping("/available")
    public Result<Map<String, Object>> getAvailableSeats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime) {
        return seatService.getAvailableSeats(date, startTime, endTime);
    }

    @PutMapping("/status/{seatId}")
    public Result<String> updateSeatStatus(
            HttpServletRequest request,
            @PathVariable Long seatId,
            @RequestParam Integer status) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return seatService.updateSeatStatus(seatId, status);
    }

    @PostMapping("/add")
    public Result<String> addSeat(HttpServletRequest request, @RequestBody Seat seat) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return seatService.addSeat(seat);
    }

    @DeleteMapping("/{seatId}")
    public Result<String> deleteSeat(HttpServletRequest request, @PathVariable Long seatId) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return seatService.deleteSeat(seatId);
    }
}
