package com.graduation.controller;

import com.graduation.dto.InboundOrderStatusUpdateDTO;
import com.graduation.entity.InboundOrders;
import com.graduation.repository.InboundOrdersRepository;
import com.graduation.service.InboundOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.graduation.common.BaseController;

import java.util.Map;

/**
 * <p>
 * 入库单 (任务) 表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/inbound-orders")
public class InboundOrdersController extends BaseController<InboundOrdersService, InboundOrders> {

    @Autowired
    private InboundOrdersRepository inboundOrdersRepository;

    @PostMapping("/create/order")
    public ResponseEntity<InboundOrders> createInboundOrder(@RequestBody InboundOrders inboundOrder) {
        inboundOrder.setCreatedAt(java.time.LocalDateTime.now());
        inboundOrder.setUpdatedAt(java.time.LocalDateTime.now());
        if (inboundOrder.getStatus() == null) {
            inboundOrder.setStatus("待处理"); // 设置默认状态为 "待处理"
        }
        InboundOrders createdOrder = inboundOrdersRepository.save(inboundOrder);
        return ResponseEntity.ok(createdOrder);
    }


    /**
     * 更新入库单的状态
     * 这个接口对应前端的 handleUpdateStatus 函数
     *
     * @param id 入库单的ID, 从URL路径中获取
     * @param statusUpdateDTO 包含新状态的请求体, Spring Boot会自动将JSON转换为该对象
     * @return 返回HTTP 200 OK表示成功
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateById(
            @PathVariable Long id,
            @RequestBody InboundOrderStatusUpdateDTO statusUpdateDTO) throws Exception {

        // 1. 根据ID查找实体，如果找不到，则抛出异常
        InboundOrders order = inboundOrdersRepository.findById(id)
                .orElseThrow(() -> new Exception("找不到ID为 " + id + " 的入库单"));


        // 2. 更新实体的状态
        order.setStatus(statusUpdateDTO.getStatus());

        // 3. 保存更新后的实体
        // 由于有 @Transactional 注解, 在方法执行完毕后，JPA会自动将更改同步到数据库。
        // 所以显式的 save 调用在某些情况下可以省略，但为了清晰，建议保留。
        inboundOrdersRepository.save(order);

        // 操作成功，返回一个 HTTP 200 OK 状态码，且响应体为空
        return ResponseEntity.ok().build();
    }


   /* *//**
     * 部分更新入库单信息 (PATCH)。
     * 这个接口精确匹配您在 Android ApiService 中定义的：
     * @PATCH("inbound-orders/{id}")
     * Call<InboundOrders> updateInboundOrderStatus(@Path("id") int id, @Body Map<String, Object> data);
     *
     * @param id    要更新的入库单ID，从URL路径中获取
     * @param updates 包含要更新字段的 Map，从请求体中获取。例如: {"status": "已完成"}
     * @return 返回更新后的 InboundOrders 对象和 HTTP 状态码
     *//*
    @PatchMapping("/{id}")
    public ResponseEntity<InboundOrders> updateInboundOrderStatus(@PathVariable("id") Integer id, @RequestBody Map<String, Object> updates) {

        // 1. 从数据库中获取当前实体
        InboundOrders existingOrder = service.getById(id);
        if (existingOrder == null) {
            // 如果找不到对应的订单，返回 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // 2. 遍历 Map，根据 key 更新相应的字段
        // 这种方式非常安全，因为它只允许更新你明确指定的字段
        updates.forEach((key, value) -> {
            switch (key) {
                case "status":
                    // 确保值的类型正确
                    if (value instanceof String) {
                        existingOrder.setStatus((String) value);
                    }
                    break;
                case "notes":
                    if (value instanceof String) {
                        existingOrder.setNotes((String) value);
                    }
                    break;
                // 在这里可以添加其他允许被 PATCH 更新的字段
                // default:
                //     // 可以选择忽略未知字段或抛出异常
                //     break;
            }
        });

        // 3. 将更新后的实体保存回数据库
        service.updateById(existingOrder);

        // 4. 返回更新后的完整实体和 200 OK 状态码
        return ResponseEntity.ok(existingOrder);
    }*/
}
