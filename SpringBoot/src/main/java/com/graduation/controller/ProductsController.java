package com.graduation.controller;

import com.graduation.entity.Products;
import com.graduation.service.ProductsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 产品主数据表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/products")
public class ProductsController extends BaseController<ProductsService, Products> {

}
