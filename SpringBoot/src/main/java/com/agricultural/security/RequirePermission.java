package com.agricultural.security;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于标注需要特定权限的方法或类
 * 支持基于角色的访问控制（RBAC）
 * 
 * 使用示例：
 * @RequirePermission(roles = {UserType.ADMIN})
 * public Result deleteUser(Long id) { ... }
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 允许访问的用户类型
     * 如果为空，则表示需要认证但不限制角色
     * 
     * @return 允许的用户类型数组
     */
    String[] roles() default {};
    
    /**
     * 权限描述
     * 用于日志记录和错误提示
     * 
     * @return 权限描述
     */
    String description() default "";
    
    /**
     * 是否需要认证
     * 默认为true，表示必须认证
     * 
     * @return 是否需要认证
     */
    boolean requireAuth() default true;
}
