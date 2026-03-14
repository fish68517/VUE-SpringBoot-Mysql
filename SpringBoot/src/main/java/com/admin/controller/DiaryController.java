package com.admin.controller;

import com.admin.entity.Diary;
import com.admin.service.DiaryService;
import com.admin.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/diaries")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @PostMapping
    public ApiResponse<Diary> createDiary(@RequestBody Diary diary) {
        Diary createdDiary = diaryService.createDiary(diary);
        if (createdDiary == null) {
            return ApiResponse.error(400, "Create diary failed");
        }
        return ApiResponse.success("Create diary success", createdDiary);
    }

    @GetMapping
    public ApiResponse<List<Diary>> getDiariesByUserId(@RequestParam Long userId) {
        return ApiResponse.success("Get diaries success", diaryService.getDiariesByUserId(userId));
    }
}
