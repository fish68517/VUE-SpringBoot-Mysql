package com.graduation.controller;

import com.graduation.entity.Inventory;
import com.graduation.service.InventoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

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

}
