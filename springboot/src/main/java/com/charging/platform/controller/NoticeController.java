package com.charging.platform.controller;

import com.charging.platform.common.PageResult;
import com.charging.platform.common.Result;
import com.charging.platform.entity.Notice;
import com.charging.platform.service.NoticeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public Result<PageResult<Notice>> page(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") long pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页条数必须大于等于1")
            @Max(value = 100, message = "每页最多查询100条") long pageSize) {
        return Result.success(noticeService.getPublishedNotices(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Notice> detail(@PathVariable Long id) {
        return Result.success(noticeService.getPublishedNotice(id));
    }
}
