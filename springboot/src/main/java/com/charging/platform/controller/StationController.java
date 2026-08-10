package com.charging.platform.controller;

import com.charging.platform.common.PageResult;
import com.charging.platform.common.Result;
import com.charging.platform.dto.StationQuery;
import com.charging.platform.entity.GuidePoint;
import com.charging.platform.service.GuideService;
import com.charging.platform.service.PileService;
import com.charging.platform.service.StationService;
import com.charging.platform.vo.PileStatusVO;
import com.charging.platform.vo.StationDetailVO;
import com.charging.platform.vo.StationListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;
    private final PileService pileService;
    private final GuideService guideService;

    @GetMapping
    public Result<PageResult<StationListVO>> page(@Valid StationQuery query) {
        return Result.success(stationService.getStationPage(query));
    }

    @GetMapping("/{id}")
    public Result<StationDetailVO> detail(@PathVariable Long id) {
        return Result.success(stationService.getStationDetail(id));
    }

    @GetMapping("/{id}/piles")
    public Result<List<PileStatusVO>> piles(@PathVariable Long id) {
        stationService.getStationDetail(id);
        return Result.success(pileService.getStationPiles(id));
    }

    @GetMapping("/{id}/guide")
    public Result<List<GuidePoint>> guide(@PathVariable Long id) {
        return Result.success(guideService.getGuidePoints(id));
    }
}
