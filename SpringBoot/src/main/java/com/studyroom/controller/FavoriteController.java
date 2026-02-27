package com.studyroom.controller;

import com.studyroom.dto.Result;
import com.studyroom.service.FavoriteSeatService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteSeatService favoriteSeatService;

    public FavoriteController(FavoriteSeatService favoriteSeatService) {
        this.favoriteSeatService = favoriteSeatService;
    }

    @PostMapping("/add/{seatId}")
    public Result<String> addFavorite(HttpServletRequest request, @PathVariable Long seatId) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteSeatService.addFavorite(userId, seatId);
    }

    @DeleteMapping("/remove/{seatId}")
    public Result<String> removeFavorite(HttpServletRequest request, @PathVariable Long seatId) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteSeatService.removeFavorite(userId, seatId);
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getMyFavorites(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteSeatService.getUserFavorites(userId);
    }

    @GetMapping("/check/{seatId}")
    public Result<Boolean> checkFavorite(HttpServletRequest request, @PathVariable Long seatId) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteSeatService.isFavorite(userId, seatId);
    }
}
