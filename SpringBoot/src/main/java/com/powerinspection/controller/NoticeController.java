package com.powerinspection.controller;

import com.powerinspection.common.ApiResponse;
import com.powerinspection.common.BusinessException;
import com.powerinspection.dto.NoticeRequest;
import com.powerinspection.entity.Notice;
import com.powerinspection.entity.NoticeStatus;
import com.powerinspection.entity.User;
import com.powerinspection.entity.UserRole;
import com.powerinspection.logging.OperationLogRecord;
import com.powerinspection.repository.NoticeRepository;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeRepository noticeRepository;
    private final ViewService viewService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) Boolean onlyActive) {
        List<Notice> notices;
        if (Boolean.TRUE.equals(onlyActive)) {
            notices = noticeRepository.findByStatusOrderByPinnedDescPublishTimeDesc(NoticeStatus.ACTIVE);
        } else {
            notices = noticeRepository.findAll().stream()
                    .sorted((a, b) -> {
                        int pinnedCompare = Boolean.compare(Boolean.TRUE.equals(b.getPinned()), Boolean.TRUE.equals(a.getPinned()));
                        if (pinnedCompare != 0) {
                            return pinnedCompare;
                        }
                        return b.getPublishTime().compareTo(a.getPublishTime());
                    })
                    .toList();
        }
        return ApiResponse.ok(notices.stream().map(viewService::noticeMap).toList());
    }

    @PostMapping
    @OperationLogRecord(module = "公告管理", action = "新增公告")
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody NoticeRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        Notice notice = new Notice();
        fill(notice, request);
        return ApiResponse.ok("新增成功", viewService.noticeMap(noticeRepository.save(notice)));
    }

    @PutMapping("/{id}")
    @OperationLogRecord(module = "公告管理", action = "修改公告")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody NoticeRequest request) {
        AuthContext.requireRole(UserRole.ADMIN);
        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new BusinessException("公告不存在"));
        fill(notice, request);
        return ApiResponse.ok("修改成功", viewService.noticeMap(noticeRepository.save(notice)));
    }

    @DeleteMapping("/{id}")
    @OperationLogRecord(module = "公告管理", action = "删除公告")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        AuthContext.requireRole(UserRole.ADMIN);
        noticeRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    private void fill(Notice notice, NoticeRequest request) {
        User currentUser = AuthContext.getUser();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setPinned(Boolean.TRUE.equals(request.getPinned()));
        notice.setStatus(request.getStatus());
        notice.setPublishTime(LocalDateTime.now());
        notice.setPublisher(currentUser);
    }
}
