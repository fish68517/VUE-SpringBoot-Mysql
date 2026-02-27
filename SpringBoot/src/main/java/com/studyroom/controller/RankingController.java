package com.studyroom.controller;

import com.studyroom.dto.Result;
import com.studyroom.service.RankingService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> getMonthlyRanking(
            @RequestParam(defaultValue = "20") int limit) {
        return rankingService.getMonthlyRanking(limit);
    }

    @GetMapping("/total")
    public Result<List<Map<String, Object>>> getTotalRanking(
            @RequestParam(defaultValue = "20") int limit) {
        return rankingService.getTotalRanking(limit);
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> getMyRanking(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return rankingService.getUserRanking(userId);
    }
}
