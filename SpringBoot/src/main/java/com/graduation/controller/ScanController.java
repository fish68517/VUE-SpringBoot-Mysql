package com.graduation.controller;

import com.graduation.dto.ScanRequest;
import com.graduation.entity.Inventory;
import com.graduation.entity.TransactionLogs;
import com.graduation.repository.InventoryRepository;
import com.graduation.repository.TransactionLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/scan") // 控制器的根路径为 /scan
public class ScanController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TransactionLogsRepository transactionLogsRepository; // 假设你有这个 Repository

    /**
     * 扫码入库接口
     * 注意：这里的“入库”逻辑是指对一个【已存在的批次】增加数量，
     * 例如，退货入库或盘点多余的货物重新入库。
     *
     * @param scanRequest 包含 batchCode 和 userId
     * @return 成功或失败的响应
     */
    @PostMapping("/inbound")
    @Transactional // **非常重要**：确保库存更新和日志记录是一个原子操作
    public ResponseEntity<String> scanInbound(@RequestBody ScanRequest scanRequest) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBatchCode(scanRequest.getBatchCode());

        if (inventoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("批次号不存在");
        }

        Inventory inventory = inventoryOpt.get();
        int quantityChange = 1; // 默认每次扫码入库数量为 1，可以根据需求调整或从请求中获取

        // 1. 更新库存数量
        int newQuantity = inventory.getQuantity() + quantityChange;
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

        // 2. 记录异动日志
        createTransactionLog(inventory, scanRequest.getUserId(), "入库", quantityChange, newQuantity, "扫码入库");

        return ResponseEntity.ok("入库成功");
    }

    /**
     * 扫码出库接口
     *
     * @param scanRequest 包含 batchCode 和 userId
     * @return 成功或失败的响应
     */
    @PostMapping("/outbound")
    @Transactional // **非常重要**
    public ResponseEntity<String> scanOutbound(@RequestBody ScanRequest scanRequest) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBatchCode(scanRequest.getBatchCode());

        if (inventoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("批次号不存在");
        }

        Inventory inventory = inventoryOpt.get();
        int quantityChange = -1; // 出库数量为负数

        // 检查库存是否充足
        if (inventory.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("库存不足，无法出库");
        }

        // 1. 更新库存数量
        int newQuantity = inventory.getQuantity() + quantityChange;
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

        // 2. 记录异动日志
        createTransactionLog(inventory, scanRequest.getUserId(), "出库", quantityChange, newQuantity, "扫码出库");

        return ResponseEntity.ok("出库成功");
    }

    /**
     * 辅助方法，用于创建异动日志
     */
    private void createTransactionLog(Inventory inventory, int userId, String type, int quantityChange, int quantityAfter, String notes) {
        TransactionLogs log = new TransactionLogs();
        log.setInventoryId(inventory.getId());
        log.setUserId(userId);
        log.setType(type);
        log.setQuantityChange(quantityChange);
        log.setQuantityAfterTransaction(quantityAfter);
        log.setNotes(notes);
        log.setCreatedAt(LocalDateTime.now()); // 假设你有这个字段并希望记录当前时间
        transactionLogsRepository.save(log);
    }
}