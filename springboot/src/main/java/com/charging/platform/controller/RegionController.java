package com.charging.platform.controller;

import com.charging.platform.common.Result;
import com.charging.platform.service.RegionService;
import com.charging.platform.vo.RegionTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/tree")
    public Result<List<RegionTreeVO>> tree() {
        return Result.success(regionService.getRegionTree());
    }
}
