package com.graduation.controller;

import com.graduation.dto.InboundOrderStatusUpdateDTO;
import com.graduation.entity.InboundOrders;
import com.graduation.repository.InboundOrdersRepository;
import com.graduation.service.InboundOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.graduation.common.BaseController;

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
}
