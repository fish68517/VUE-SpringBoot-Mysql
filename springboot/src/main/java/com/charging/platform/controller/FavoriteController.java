package com.charging.platform.controller;

import com.charging.platform.common.PageResult;
import com.charging.platform.common.Result;
import com.charging.platform.dto.FavoriteRequest;
import com.charging.platform.service.FavoriteService;
import com.charging.platform.vo.FavoriteStateVO;
import com.charging.platform.vo.FavoriteVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public Result<PageResult<FavoriteVO>> page(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") long pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页条数必须大于等于1")
            @Max(value = 100, message = "每页最多查询100条") long pageSize) {
        return Result.success(favoriteService.getFavorites(userId, pageNum, pageSize));
    }

    @GetMapping("/check")
    public Result<FavoriteStateVO> check(@RequestParam Long userId, @RequestParam Long stationId) {
        return Result.success(favoriteService.check(userId, stationId));
    }

    @PostMapping
    public Result<FavoriteStateVO> add(@Valid @RequestBody FavoriteRequest request) {
        return Result.success(favoriteService.add(request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        favoriteService.remove(id);
        return Result.success();
    }
}
