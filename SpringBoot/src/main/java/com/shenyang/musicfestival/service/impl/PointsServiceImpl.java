package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.dto.*;
import com.shenyang.musicfestival.entity.*;
import com.shenyang.musicfestival.repository.*;
import com.shenyang.musicfestival.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final CheckinTaskRepository taskRepo;
    private final CheckinRecordRepository recordRepo;
    private final PointsMallItemRepository mallRepo;
    private final PointsExchangeRepository exchangeRepo;
    private final PointsHistoryRepository historyRepo;
    private final UserRepository userRepo;

    @Override
    public List<CheckinTaskDTO> getTasks(Long userId) {
        return taskRepo.findAll().stream().map(task -> {
            boolean isCompleted = recordRepo.existsByUserIdAndTaskId(userId, task.getId());
            return CheckinTaskDTO.builder()
                    .id(task.getId())
                    .name(task.getName())
                    .description(task.getDescription())
                    .points(task.getPoints())
                    .status(isCompleted ? "completed" : task.getStatus())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkin(Long userId, Long taskId) {
        if (recordRepo.existsByUserIdAndTaskId(userId, taskId)) {
            throw new IllegalArgumentException("您已经打卡过了");
        }
        CheckinTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("打卡任务不存在"));

        // 1. 创建打卡记录（毕设演示写入默认坐标和假图片）
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setTaskId(taskId);
        record.setPhoto("demo_photo.jpg");
        record.setLatitude(new BigDecimal("0"));
        record.setLongitude(new BigDecimal("0"));
        record.setStatus("approved"); // 直接审核通过
        record = recordRepo.save(record);

        // 2. 增加用户积分
        User user = userRepo.findById(userId).orElseThrow();
        Long currentPoints = user.getPoints() == null ? 0L : user.getPoints();
        user.setPoints(currentPoints + task.getPoints());
        userRepo.save(user);

        // 3. 增加积分流水
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setChangeAmount(task.getPoints());
        history.setChangeType("checkin");
        history.setRelatedId(record.getId());
        history.setDescription("完成打卡任务：" + task.getName());
        historyRepo.save(history);
    }

    @Override
    public List<PointsMallItemDTO> getMallItems() {
        return mallRepo.findByIsActiveTrue().stream().map(item -> {
            // 解析 JSON 图片字段取出第一张
            String imageUrl = "https://via.placeholder.com/300x300?text=Gift";
            if (item.getImages() != null && item.getImages().contains("\"")) {
                try {
                    List<String> images = new com.fasterxml.jackson.databind.ObjectMapper().readValue(item.getImages(), List.class);
                    if (!images.isEmpty()) imageUrl = images.get(0);
                } catch (Exception e) {}
            }
            return PointsMallItemDTO.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .pointsRequired(item.getPointsRequired())
                    .stock(item.getStock())
                    .image(imageUrl)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void exchangeItem(Long userId, Long itemId, String address) {
        PointsMallItem item = mallRepo.findById(itemId).orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        if (item.getStock() <= 0) throw new IllegalArgumentException("商品已被兑换完啦");

        User user = userRepo.findById(userId).orElseThrow();
        Long currentPoints = user.getPoints() == null ? 0L : user.getPoints();
        if (currentPoints < item.getPointsRequired()) {
            throw new IllegalArgumentException("您的积分不足，快去打卡赚取积分吧");
        }

        // 1. 扣减积分与库存
        user.setPoints(currentPoints - item.getPointsRequired());
        userRepo.save(user);

        item.setStock(item.getStock() - 1);
        mallRepo.save(item);

        // 2. 生成兑换订单
        PointsExchange exchange = new PointsExchange();
        exchange.setUserId(userId);
        exchange.setMallItemId(itemId);
        exchange.setPointsUsed(item.getPointsRequired());
        exchange.setStatus("pending"); // 待发货
        exchange.setShippingAddress(address == null ? "虚拟物品无需地址" : address);
        exchange = exchangeRepo.save(exchange);

        // 3. 记录积分流水
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setChangeAmount(-item.getPointsRequired());
        history.setChangeType("exchange");
        history.setRelatedId(exchange.getId());
        history.setDescription("兑换商城商品：" + item.getName());
        historyRepo.save(history);
    }

    @Override
    public List<PointsHistoryDTO> getHistory(Long userId) {
        return historyRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(h -> PointsHistoryDTO.builder()
                        .id(h.getId())
                        .changeAmount(h.getChangeAmount())
                        .description(h.getDescription())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Long getBalance(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        return user.getPoints() == null ? 0L : user.getPoints();
    }
}