package com.graduation.controller;

import com.graduation.common.BaseController;
import com.graduation.entity.UserResourceAction;
import com.graduation.service.UserResourceActionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userResourceAction")
public class UserResourceActionController extends BaseController<UserResourceActionService, UserResourceAction> {

    /**
     * 执行点赞或收藏 (供App端调用)
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @param type 操作类型: "like" 或 "collect"
     */
    @PostMapping("/toggle/{userId}/{resourceId}/{type}")
    public boolean toggle(@PathVariable Long userId, @PathVariable Long resourceId, @PathVariable String type) {
        service.toggleAction(userId, resourceId, type);
        return true;
    }
}