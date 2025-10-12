package com.graduation.controller;

import com.graduation.entity.Roles;
import com.graduation.service.RolesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 用户角色表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/roles")
public class RolesController extends BaseController<RolesService, Roles> {

}
