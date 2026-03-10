package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.entity.*;
import com.shenyang.musicfestival.repository.*;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/ticket")
@RequiredArgsConstructor
public class AdminTicketController {

    private final TicketSessionRepository sessionRepository;
    private final TicketZoneRepository zoneRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    // ==========================================
    // 1. 场次管理 (Sessions)
    // ==========================================
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<TicketSession>> createSession(@RequestBody TicketSession session) {
        // 默认关联到音乐节ID 1
        session.setFestivalId(1L);
        return ResponseEntity.ok(ApiResponse.success(sessionRepository.save(session), "创建场次成功"));
    }

    @PutMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<TicketSession>> updateSession(@PathVariable Long id, @RequestBody TicketSession sessionDetails) {
        TicketSession session = sessionRepository.findById(id).orElseThrow();
        session.setName(sessionDetails.getName());
        session.setStartTime(sessionDetails.getStartTime());
        session.setEndTime(sessionDetails.getEndTime());
        session.setStatus(sessionDetails.getStatus());
        return ResponseEntity.ok(ApiResponse.success(sessionRepository.save(session), "更新场次成功"));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        sessionRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "删除场次成功"));
    }

    // ==========================================
    // 2. 分区管理 (Zones)
    // ==========================================

    // 解决报错 1：获取某场次的分区列表 /api/admin/ticket/sessions/1/zones
    @GetMapping("/sessions/{sessionId}/zones")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getZonesBySession(@PathVariable Long sessionId) {
        // 使用流过滤，防止 Repository 没写对应方法导致报错
        List<TicketZone> zones = zoneRepository.findAll().stream()
                .filter(z -> z.getSessionId().equals(sessionId))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = zones.stream().map(zone -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", zone.getId());
            map.put("sessionId", zone.getSessionId());
            map.put("name", zone.getName());
            map.put("price", zone.getPrice());
            map.put("totalCapacity", zone.getTotalCapacity());
            int soldCount = zone.getSoldCount() == null ? 0 : zone.getSoldCount();
            map.put("soldCount", soldCount);
            map.put("remainingCapacity", zone.getTotalCapacity() - soldCount); // 前端需要的剩余库存
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result, "获取分区成功"));
    }

    @PostMapping("/zones")
    public ResponseEntity<ApiResponse<TicketZone>> createZone(@RequestBody TicketZone zone) {
        if (zone.getSoldCount() == null) zone.setSoldCount(0);
        return ResponseEntity.ok(ApiResponse.success(zoneRepository.save(zone), "创建分区成功"));
    }

    @PutMapping("/zones/{id}")
    public ResponseEntity<ApiResponse<TicketZone>> updateZone(@PathVariable Long id, @RequestBody TicketZone zoneDetails) {
        TicketZone zone = zoneRepository.findById(id).orElseThrow();
        zone.setName(zoneDetails.getName());
        zone.setPrice(zoneDetails.getPrice());
        return ResponseEntity.ok(ApiResponse.success(zoneRepository.save(zone), "更新分区成功"));
    }

    @PostMapping("/zones/{zoneId}/stock")
    public ResponseEntity<ApiResponse<Void>> adjustStock(@PathVariable Long zoneId, @RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        Integer count = (Integer) body.get("count");

        TicketZone zone = zoneRepository.findById(zoneId).orElseThrow();
        if ("add".equals(action)) {
            zone.setTotalCapacity(zone.getTotalCapacity() + count); // 增加总库存 = 补票
        } else if ("reduce".equals(action)) {
            // 防止减除的库存导致剩余库存变成负数
            int currentRemaining = zone.getTotalCapacity() - (zone.getSoldCount() == null ? 0 : zone.getSoldCount());
            if (count > currentRemaining) {
                throw new IllegalArgumentException("减少的库存不能大于剩余库存");
            }
            zone.setTotalCapacity(zone.getTotalCapacity() - count);
        }
        zoneRepository.save(zone);
        return ResponseEntity.ok(ApiResponse.success(null, "调库成功"));
    }

    // ==========================================
    // 3. 票务订单管理 (Orders)
    // ==========================================

    // 解决报错 2：获取票务订单列表 /api/admin/ticket/orders
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) String keyword) {

        // 1. 查出所有 orderType 为 'ticket' 的订单，按时间倒序
        List<Order> ticketOrders = orderRepository.findAll().stream()
                .filter(o -> "ticket".equals(o.getOrderType()))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());

        List<Map<String, Object>> dtoList = new ArrayList<>();

        // 2. 遍历订单，组装详细数据供前端完美展示
        for (Order order : ticketOrders) {
            User user = userRepository.findById(order.getUserId()).orElse(null);
            String phone = user != null ? user.getPhone() : "未知账号";

            // 关键词搜索过滤 (匹配订单号或手机号)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String orderIdStr = String.valueOf(order.getId());
                if (!phone.contains(keyword) && !orderIdStr.contains(keyword)) {
                    continue;
                }
            }

            // 找寻该用户购买的门票（为了演示效果，直接提取用户所有的票务信息映射到订单里）
            List<Ticket> tickets = ticketRepository.findAll().stream()
                    .filter(t -> t.getUserId().equals(order.getUserId()))
                    .collect(Collectors.toList());

            // 场次过滤下拉框逻辑
            if (sessionId != null) {
                boolean matchSession = tickets.stream().anyMatch(t -> sessionId.equals(t.getSessionId()));
                if (!matchSession && !tickets.isEmpty()) continue;
            }

            List<Map<String, Object>> items = tickets.stream().map(t -> {
                TicketZone zone = zoneRepository.findById(t.getZoneId()).orElse(null);
                Map<String, Object> item = new HashMap<>();
                item.put("realName", t.getBuyerName());
                item.put("idNumber", t.getBuyerIdNumber());
                item.put("zoneName", zone != null ? zone.getName() : "未知分区");
                item.put("price", zone != null ? zone.getPrice() : BigDecimal.ZERO);
                return item;
            }).collect(Collectors.toList());

            Map<String, Object> dto = new HashMap<>();
            dto.put("id", order.getId());
            dto.put("orderNo", "TKT" + 100000 + order.getId()); // 生成一个逼真的订单编号
            dto.put("buyerPhone", phone);
            dto.put("totalAmount", order.getTotalAmount());
            dto.put("status", order.getStatus());
            dto.put("createdAt", order.getCreatedAt());
            dto.put("items", items);
            dtoList.add(dto);
        }

        // 3. 手动进行分页切割 (防止报错)
        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        List<Map<String, Object>> pagedList = dtoList.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> resultPage = new PageImpl<>(pagedList, pageable, dtoList.size());

        return ResponseEntity.ok(ApiResponse.success(resultPage, "获取票务订单成功"));
    }
}