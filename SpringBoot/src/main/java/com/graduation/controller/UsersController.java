package com.graduation.controller;

import com.graduation.entity.Users;
import com.graduation.service.UsersService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 系统用户账户表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/users")
public class UsersController extends BaseController<UsersService, Users> {

}
