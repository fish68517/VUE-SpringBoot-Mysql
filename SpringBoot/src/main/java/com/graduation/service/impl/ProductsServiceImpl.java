package com.graduation.service.impl;

import com.graduation.entity.Products;
import com.graduation.mapper.ProductsMapper;
import com.graduation.service.ProductsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品主数据表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@Service
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements ProductsService {

}
