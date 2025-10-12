package com.graduation.controller;

import com.graduation.entity.Departments;
import com.graduation.service.DepartmentsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 部门信息表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/departments")
public class DepartmentsController extends BaseController<DepartmentsService, Departments> {

}
