package com.graduation.service.impl;

import com.graduation.entity.TransactionLogs;
import com.graduation.mapper.TransactionLogsMapper;
import com.graduation.service.TransactionLogsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存异动日志表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Service
public class TransactionLogsServiceImpl extends ServiceImpl<TransactionLogsMapper, TransactionLogs> implements TransactionLogsService {

}
