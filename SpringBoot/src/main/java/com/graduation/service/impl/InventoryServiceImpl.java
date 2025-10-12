package com.graduation.service.impl;

import com.graduation.entity.Inventory;
import com.graduation.mapper.InventoryMapper;
import com.graduation.service.InventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存及批次表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

}
