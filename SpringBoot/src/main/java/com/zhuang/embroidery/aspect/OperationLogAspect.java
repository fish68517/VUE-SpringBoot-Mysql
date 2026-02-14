package com.zhuang.embroidery.aspect;

import com.zhuang.embroidery.annotation.OperationLog;
import com.zhuang.embroidery.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志 AOP 切面
 * 自动记录被 @OperationLog 注解标记的方法的操作日志
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    /**
     * 在方法执行成功后记录操作日志
     *
     * @param joinPoint 连接点
     * @param operationLog 操作日志注解
     */
    @AfterReturning("@annotation(operationLog)")
    public void recordOperationLog(JoinPoint joinPoint, OperationLog operationLog) {
        try {
            // 获取用户ID（从请求头或参数中）
            Long userId = extractUserId(joinPoint);
            
            if (userId == null) {
                log.debug("无法提取用户ID，跳过操作日志记录");
                return;
            }

            // 获取目标ID
            Long targetId = extractTargetId(joinPoint, operationLog);

            // 记录操作日志
            operationLogService.recordOperationLog(
                    userId,
                    operationLog.action(),
                    operationLog.targetType(),
                    targetId
            );

            log.debug("操作日志记录成功: userId={}, action={}, targetType={}, targetId={}",
                    userId, operationLog.action(), operationLog.targetType(), targetId);
        } catch (Exception e) {
            log.error("记录操作日志异常", e);
            // 不抛出异常，避免影响业务流程
        }
    }

    /**
     * 从请求头或方法参数中提取用户ID
     *
     * @param joinPoint 连接点
     * @return 用户ID
     */
    private Long extractUserId(JoinPoint joinPoint) {
        try {
            // 首先尝试从请求头中获取用户ID
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userIdHeader = request.getHeader("X-User-Id");
                if (userIdHeader != null && !userIdHeader.isEmpty()) {
                    try {
                        return Long.parseLong(userIdHeader);
                    } catch (NumberFormatException e) {
                        log.debug("无效的用户ID头: {}", userIdHeader);
                    }
                }
            }

            // 尝试从方法参数中获取用户ID
            Object[] args = joinPoint.getArgs();
            String[] paramNames = getMethodParameterNames(joinPoint);

            for (int i = 0; i < paramNames.length; i++) {
                if ("userId".equalsIgnoreCase(paramNames[i]) && args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }

            log.debug("无法从请求头或参数中提取用户ID");
            return null;
        } catch (Exception e) {
            log.debug("提取用户ID异常", e);
            return null;
        }
    }

    /**
     * 从方法参数中提取目标ID
     *
     * @param joinPoint 连接点
     * @param operationLog 操作日志注解
     * @return 目标ID
     */
    private Long extractTargetId(JoinPoint joinPoint, OperationLog operationLog) {
        try {
            String targetIdParamName = operationLog.targetIdParamName();
            if (targetIdParamName == null || targetIdParamName.isEmpty()) {
                return null;
            }

            Object[] args = joinPoint.getArgs();
            String[] paramNames = getMethodParameterNames(joinPoint);

            for (int i = 0; i < paramNames.length; i++) {
                if (targetIdParamName.equalsIgnoreCase(paramNames[i]) && args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }

            return null;
        } catch (Exception e) {
            log.debug("提取目标ID异常", e);
            return null;
        }
    }

    /**
     * 获取方法的参数名称
     *
     * @param joinPoint 连接点
     * @return 参数名称数组
     */
    private String[] getMethodParameterNames(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature())
                    .getParameterTypes();
            Class<?> targetClass = joinPoint.getTarget().getClass();

            Method method = targetClass.getMethod(methodName, parameterTypes);
            java.lang.reflect.Parameter[] parameters = method.getParameters();

            String[] paramNames = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                paramNames[i] = parameters[i].getName();
            }

            return paramNames;
        } catch (Exception e) {
            log.debug("获取方法参数名称异常", e);
            return new String[0];
        }
    }
}
