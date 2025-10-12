package com.graduation.controller;

import com.graduation.entity.InboundOrders;
import com.graduation.service.InboundOrdersService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/inboundOrders")
public class InboundOrdersController extends BaseController<InboundOrdersService, InboundOrders> {

}
