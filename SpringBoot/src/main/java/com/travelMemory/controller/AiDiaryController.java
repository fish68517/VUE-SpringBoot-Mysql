package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.AiDiaryGenerateRequest;
import com.travelMemory.dto.AiDiaryResponse;
import com.travelMemory.service.AiDiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/diary")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class AiDiaryController {

    private final AiDiaryService aiDiaryService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<AiDiaryResponse>> generateDiary(
            @Valid @RequestBody AiDiaryGenerateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(aiDiaryService.generateDiary(request)));
    }
}
