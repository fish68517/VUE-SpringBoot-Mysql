package com.charging.platform.controller;

import com.charging.platform.common.Result;
import com.charging.platform.service.HomeService;
import com.charging.platform.service.StationService;
import com.charging.platform.vo.HomeOverviewVO;
import com.charging.platform.vo.StationListVO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final StationService stationService;

    @GetMapping("/overview")
    public Result<HomeOverviewVO> overview() {
        return Result.success(homeService.getOverview());
    }

    @GetMapping("/recommendations")
    public Result<List<StationListVO>> recommendations(
            @RequestParam(defaultValue = "6")
            @Min(value = 1, message = "推荐数量必须大于等于1")
            @Max(value = 12, message = "推荐数量不能超过12") int limit) {
        return Result.success(stationService.getRecommendations(limit));
    }
}
