package com.charging.platform.controller;

import com.charging.platform.common.Result;
import com.charging.platform.entity.PileRealtimeStatus;
import com.charging.platform.service.PileService;
import com.charging.platform.vo.PileStatusVO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/piles")
@RequiredArgsConstructor
public class PileController {

    private final PileService pileService;

    @GetMapping("/{id}")
    public Result<PileStatusVO> detail(@PathVariable Long id) {
        return Result.success(pileService.getPile(id));
    }

    @GetMapping("/{id}/status-history")
    public Result<List<PileRealtimeStatus>> history(
            @PathVariable Long id,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "查询条数必须大于等于1")
            @Max(value = 100, message = "查询条数不能超过100") int limit) {
        return Result.success(pileService.getStatusHistory(id, limit));
    }
}
