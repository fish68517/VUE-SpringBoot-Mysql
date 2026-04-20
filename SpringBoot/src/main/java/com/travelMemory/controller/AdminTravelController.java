package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CreateTravelRecordRequest;
import com.travelMemory.dto.TravelRecordResponse;
import com.travelMemory.service.AdminTravelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/travels")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminTravelController {

    private final AdminTravelService adminTravelService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TravelRecordResponse>>> getTravelRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TravelRecordResponse> records = adminTravelService.getTravelRecords(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelRecordResponse>> getTravelRecord(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminTravelService.getTravelRecord(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelRecordResponse>> updateTravelRecord(
            @PathVariable Long id,
            @Valid @RequestBody CreateTravelRecordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminTravelService.updateTravelRecord(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTravelRecord(@PathVariable Long id) {
        adminTravelService.deleteTravelRecord(id);
        return ResponseEntity.ok(ApiResponse.success("旅行记录删除成功", null));
    }
}
