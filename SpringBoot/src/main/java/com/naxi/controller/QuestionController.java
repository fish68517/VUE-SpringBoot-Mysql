package com.naxi.controller;

import com.naxi.common.ApiResponse;
import com.naxi.entity.Pattern;
import com.naxi.entity.Question;
import com.naxi.entity.User;
import com.naxi.service.PatternService;
import com.naxi.service.QuestionService;
import com.naxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private PatternService patternService;

    /**
     * 发布提问
     * 需求: 22.1
     */
    @PostMapping
    public ApiResponse<?> publishQuestion(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long patternId = ((Number) request.get("patternId")).longValue();
            String title = (String) request.get("title");
            String content = (String) request.get("content");

            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            if (patternId == null) {
                return ApiResponse.error(400, "纹样ID不能为空");
            }
            if (title == null || title.trim().isEmpty()) {
                return ApiResponse.error(400, "提问标题不能为空");
            }
            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.error(400, "提问内容不能为空");
            }

            // 验证用户是否存在
            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error(1001, "用户不存在");
            }

            // 验证纹样是否存在
            Pattern pattern = patternService.getPatternById(patternId);
            if (pattern == null) {
                return ApiResponse.error(2001, "纹样不存在");
            }

            Question question = questionService.publishQuestion(userId, patternId, title, content);
            return ApiResponse.success("提问发布成功", question);
        } catch (Exception e) {
            return ApiResponse.error(500, "发布提问失败: " + e.getMessage());
        }
    }

    /**
     * 获取提问列表
     * 需求: 22.1
     */
    @GetMapping
    public ApiResponse<?> getQuestions(
            @RequestParam Long patternId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (patternId == null) {
                return ApiResponse.error(400, "纹样ID不能为空");
            }

            // 验证纹样是否存在
            Pattern pattern = patternService.getPatternById(patternId);
            if (pattern == null) {
                return ApiResponse.error(2001, "纹样不存在");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Question> questions = questionService.getPatternQuestions(patternId, pageable);

            // 构建响应数据，包含用户信息
            List<Map<String, Object>> response = new ArrayList<>();
            for (Question question : questions.getContent()) {
                User user = userService.getUserById(question.getUserId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", question.getId());
                item.put("userId", question.getUserId());
                item.put("patternId", question.getPatternId());
                item.put("title", question.getTitle());
                item.put("content", question.getContent());
                item.put("createdAt", question.getCreatedAt());
                item.put("updatedAt", question.getUpdatedAt());
                if (user != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("nickname", user.getNickname());
                    userInfo.put("avatarUrl", user.getAvatarUrl());
                    item.put("user", userInfo);
                }
                response.add(item);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("content", response);
            result.put("totalElements", questions.getTotalElements());
            result.put("totalPages", questions.getTotalPages());
            result.put("currentPage", page);
            result.put("pageSize", size);

            return ApiResponse.success("获取提问列表成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取提问列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除提问（管理员）
     * 需求: 22.1
     */
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteQuestion(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ApiResponse.error(400, "提问ID无效");
            }

            Question question = questionService.getQuestionById(id);
            if (question == null) {
                return ApiResponse.error(404, "提问不存在");
            }

            boolean deleted = questionService.deleteQuestion(id);
            if (deleted) {
                return ApiResponse.success("删除提问成功");
            } else {
                return ApiResponse.error(500, "删除提问失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "删除提问失败: " + e.getMessage());
        }
    }
}
