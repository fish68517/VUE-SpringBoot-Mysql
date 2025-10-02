package com.graduation.controller;

import com.graduation.entity.CampusUser;
import com.graduation.service.CampusUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 校园用户表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-02
 */
@RestController
@RequestMapping("/campusUser")
public class CampusUserController extends BaseController<CampusUserService, CampusUser> {

}
