package com.graduation.controller;

import com.graduation.entity.TransactionLogs;
import com.graduation.service.TransactionLogsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 库存异动日志表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/transaction-logs")
public class TransactionLogsController extends BaseController<TransactionLogsService, TransactionLogs> {

}
