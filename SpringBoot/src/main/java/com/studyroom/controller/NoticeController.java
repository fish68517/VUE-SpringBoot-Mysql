package com.studyroom.controller;

import com.studyroom.dto.Result;
import com.studyroom.entity.Notice;
import com.studyroom.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/list")
    public Result<List<Notice>> getPublishedNotices() {
        return noticeService.getPublishedNotices();
    }

    @GetMapping("/all")
    public Result<List<Notice>> getAllNotices(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        log.info("getAllNotices - role from request: {}", role);
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return noticeService.getAllNotices();
    }

    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    @PostMapping("/create")
    public Result<String> createNotice(HttpServletRequest request, @RequestBody Notice notice) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        Long userId = (Long) request.getAttribute("userId");
        return noticeService.createNotice(notice, userId);
    }

    @PutMapping("/update")
    public Result<String> updateNotice(HttpServletRequest request, @RequestBody Notice notice) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return noticeService.updateNotice(notice);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteNotice(HttpServletRequest request, @PathVariable Long id) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return noticeService.deleteNotice(id);
    }

    @PutMapping("/status/{id}")
    public Result<String> updateNoticeStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam Integer status) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error(403, "无权限");
        }
        return noticeService.updateNoticeStatus(id, status);
    }
}
