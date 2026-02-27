package com.studyroom.controller;

import com.studyroom.dto.ReservationDTO;
import com.studyroom.dto.Result;
import com.studyroom.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/create")
    public Result<String> createReservation(HttpServletRequest request, @RequestBody @Validated ReservationDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return reservationService.createReservation(userId, dto);
    }

    @PostMapping("/cancel/{id}")
    public Result<String> cancelReservation(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return reservationService.cancelReservation(userId, id);
    }

    @PostMapping("/checkin/{id}")
    public Result<String> checkIn(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return reservationService.checkIn(userId, id);
    }

    @PostMapping("/checkout/{id}")
    public Result<String> checkOut(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return reservationService.checkOut(userId, id);
    }

    @GetMapping("/my")
    public Result<List<Map<String, Object>>> getMyReservations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return reservationService.getUserReservations(userId);
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getAllReservations(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return reservationService.getAllReservations(date);
    }

    @PostMapping("/change/{id}")
    public Result<String> changeReservation(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody @Validated ReservationDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return reservationService.changeReservation(userId, id, dto);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return reservationService.getStatistics();
    }
}
