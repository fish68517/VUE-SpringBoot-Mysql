package com.graduation.controller;

import com.graduation.entity.Inventory;
import com.graduation.repository.InventoryRepository;
import com.graduation.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

import java.util.List;

/**
 * <p>
 * 库存及批次表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController extends BaseController<InventoryService, Inventory> {

    @Autowired
    private InventoryRepository inventoryRepository;



    /**
     * 根据入库单ID查询其下所有的库存批次。
     *
     * 这个接口与您在 Android ApiService 中定义的
     * @GET("inventory/inboundId/{inboundId}")
     * 完全匹配。
     *
     * @param inboundId URL路径中的入库单ID
     * @return 返回一个包含批次详情的列表和 HTTP 状态码 200 OK
     */
    @GetMapping("/inboundId/{inboundId}")
    public ResponseEntity<List<Inventory>> getInventoryByInboundId(@PathVariable("inboundId") Integer inboundId) {
        // 调用 service 层的方法来执行业务逻辑
        List<Inventory> inventoryList = inventoryRepository.findByInboundOrderId(inboundId);;

        // 使用 ResponseEntity.ok() 来包装返回的数据，它会自动设置 HTTP 200 状态码
        return ResponseEntity.ok(inventoryList);
    }

}
