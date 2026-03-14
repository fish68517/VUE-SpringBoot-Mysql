package com.admin.service;

import com.admin.entity.Diary;
import com.admin.mapper.DiaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DiaryService {

    @Autowired
    private DiaryMapper diaryMapper;

    public Diary createDiary(Diary diary) {
        if (diary == null || diary.getUserId() == null || diary.getUserId() <= 0) {
            return null;
        }
        if (diary.getContent() == null || diary.getContent().trim().isEmpty()) {
            return null;
        }
        if (diary.getMood() == null || diary.getMood() < 1 || diary.getMood() > 3) {
            return null;
        }

        diary.setContent(diary.getContent().trim());
        int result = diaryMapper.insert(diary);
        return result > 0 ? diary : null;
    }

    public List<Diary> getDiariesByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return Collections.emptyList();
        }
        return diaryMapper.findByUserId(userId);
    }
}
