package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.entity.PointsExchange;
import com.shenyang.musicfestival.entity.PointsMallItem;
import com.shenyang.musicfestival.entity.User;
import com.shenyang.musicfestival.repository.PointsExchangeRepository;
import com.shenyang.musicfestival.repository.PointsMallItemRepository;
import com.shenyang.musicfestival.repository.UserRepository;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/points")
@RequiredArgsConstructor
public class AdminPointsController {

    private final PointsMallItemRepository mallItemRepository;
    private final PointsExchangeRepository exchangeRepository;
    private final UserRepository userRepository;

    // ==========================================
    // 1. 获取积分商品列表 (兼容前端 PointsGood 接口)
    // 解决报错：/api/admin/points/goods?page=0&size=10
    // ==========================================
    @GetMapping("/goods")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getGoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        List<PointsMallItem> allItems = mallItemRepository.findAll();

        // 内存过滤与排序
        List<PointsMallItem> filteredItems = allItems.stream()
                .filter(item -> keyword == null || keyword.trim().isEmpty() ||
                        (item.getName() != null && item.getName().contains(keyword)))
                .sorted((i1, i2) -> i2.getCreatedAt().compareTo(i1.getCreatedAt()))
                .collect(Collectors.toList());

        List<Map<String, Object>> dtoList = new ArrayList<>();
        for (PointsMallItem item : filteredItems) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", item.getId());
            dto.put("name", item.getName());

            // 解析后端的 images JSON，取第一张给前端的 image 字段
            String imageUrl = "";
            if (item.getImages() != null && item.getImages().contains("\"")) {
                try {
                    imageUrl = item.getImages().replaceAll("[\\[\\]\"]", "").split(",")[0].trim();
                } catch (Exception e) {}
            }
            dto.put("image", imageUrl);

            dto.put("type", item.getType());
            dto.put("pointsPrice", item.getPointsRequired()); // 字段名适配前端
            dto.put("stock", item.getStock());
            dto.put("isActive", item.getIsActive());
            dto.put("createdAt", item.getCreatedAt());
            dtoList.add(dto);
        }

        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        List<Map<String, Object>> pagedList = dtoList.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> resultPage = new PageImpl<>(pagedList, pageable, dtoList.size());

        return ResponseEntity.ok(ApiResponse.success(resultPage, "获取积分商品列表成功"));
    }

    // ==========================================
    // 2. 新增或修改积分商品 (前端统一用 POST)
    // ==========================================
    @PostMapping("/goods")
    public ResponseEntity<ApiResponse<Void>> saveGood(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("id");
        PointsMallItem item;

        if (idObj != null) {
            // 编辑更新
            Long id = Long.valueOf(idObj.toString());
            item = mallItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        } else {
            // 新增
            item = new PointsMallItem();
        }

        item.setName((String) body.get("name"));
        item.setType((String) body.get("type"));

        // 前端传过来的是 pointsPrice，后端存 pointsRequired
        item.setPointsRequired(Integer.valueOf(body.get("pointsPrice").toString()));
        item.setStock(Integer.valueOf(body.get("stock").toString()));
        item.setIsActive((Boolean) body.get("isActive"));

        // 前端传的是单张 image 字符串，后端包装成 JSON 数组字符串存入
        String imageUrl = (String) body.get("image");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            item.setImages("[\"" + imageUrl + "\"]");
        } else {
            item.setImages("[]");
        }

        mallItemRepository.save(item);
        return ResponseEntity.ok(ApiResponse.success(null, "保存商品成功"));
    }

    // ==========================================
    // 3. 删除积分商品
    // ==========================================
    @DeleteMapping("/goods/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGood(@PathVariable Long id) {
        mallItemRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "删除商品成功"));
    }

    // ==========================================
    // 4. 获取积分兑换订单列表 (带分页、状态过滤)
    // 解决报错：/api/admin/points/orders?page=0&size=10
    // ==========================================
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getExchangeOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        List<PointsExchange> allExchanges = exchangeRepository.findAll().stream()
                .sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()))
                .collect(Collectors.toList());

        List<Map<String, Object>> dtoList = new ArrayList<>();

        for (PointsExchange ex : allExchanges) {
            if (status != null && !status.trim().isEmpty() && !status.equals(ex.getStatus())) {
                continue; // 状态过滤
            }

            User user = userRepository.findById(ex.getUserId()).orElse(null);
            PointsMallItem item = mallItemRepository.findById(ex.getMallItemId()).orElse(null);

            Map<String, Object> dto = new HashMap<>();
            dto.put("id", ex.getId());
            dto.put("userId", ex.getUserId());
            dto.put("userPhone", user != null ? user.getPhone() : "未知用户");
            dto.put("goodName", item != null ? item.getName() : "已下架商品");
            dto.put("pointsCost", ex.getPointsUsed());
            dto.put("status", ex.getStatus());

            // 组装发货信息
            String shippingInfo = ex.getShippingAddress() != null ? ex.getShippingAddress() : "无";
            if (ex.getTrackingNumber() != null && !ex.getTrackingNumber().isEmpty()) {
                shippingInfo += " (物流单号: " + ex.getTrackingNumber() + ")";
            }
            dto.put("shippingInfo", shippingInfo);
            dto.put("createdAt", ex.getCreatedAt());

            dtoList.add(dto);
        }

        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        List<Map<String, Object>> pagedList = dtoList.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> resultPage = new PageImpl<>(pagedList, pageable, dtoList.size());

        return ResponseEntity.ok(ApiResponse.success(resultPage, "获取兑换订单成功"));
    }

    // ==========================================
    // 5. 审核 / 发货 积分订单
    // ==========================================
    @PostMapping("/orders/{id}/audit")
    public ResponseEntity<ApiResponse<Void>> auditExchange(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        PointsExchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("兑换记录不存在"));

        String status = body.get("status");
        String shippingNo = body.get("shippingNo");

        if (status != null && !status.trim().isEmpty()) {
            exchange.setStatus(status);
        }
        if (shippingNo != null && !shippingNo.trim().isEmpty()) {
            exchange.setTrackingNumber(shippingNo);
        }

        exchangeRepository.save(exchange);
        return ResponseEntity.ok(ApiResponse.success(null, "审核/发货操作成功"));
    }
}