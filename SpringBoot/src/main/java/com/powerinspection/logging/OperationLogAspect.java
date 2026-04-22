package com.powerinspection.logging;

import com.powerinspection.entity.User;
import com.powerinspection.security.AuthContext;
import com.powerinspection.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Around("@annotation(record)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLogRecord record) throws Throwable {
        User user = null;
        try {
            user = AuthContext.getUser();
        } catch (Exception ignored) {
        }

        try {
            Object result = joinPoint.proceed();
            operationLogService.save(record.module(), record.action(), true, "操作成功",
                    operationLogService.sanitizeArgs(joinPoint.getArgs()), user);
            return result;
        } catch (Throwable throwable) {
            operationLogService.save(record.module(), record.action(), false, throwable.getMessage(),
                    operationLogService.sanitizeArgs(joinPoint.getArgs()), user);
            throw throwable;
        }
    }
}
