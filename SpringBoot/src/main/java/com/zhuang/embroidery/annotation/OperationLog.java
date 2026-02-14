package com.zhuang.embroidery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 操作类型
     */
    String action();

    /**
     * 目标类型
     */
    String targetType() default "";

    /**
     * 目标ID参数名称（用于从方法参数中提取）
     */
    String targetIdParamName() default "";
}
