package com.travelMemory.security;

import com.travelMemory.exception.AccessDeniedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Aspect for handling permission checking annotations
 */
@Aspect
@Component
public class PermissionCheckAspect {

    @Autowired
    private PermissionService permissionService;

    /**
     * Check if the current user is the owner of the resource before method execution
     * @param joinPoint the join point
     * @throws AccessDeniedException if the current user is not the owner
     */
    @Before("@annotation(com.travelMemory.security.RequireResourceOwner)")
    public void checkResourceOwner(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireResourceOwner annotation = method.getAnnotation(RequireResourceOwner.class);

        String paramName = annotation.paramName();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Long resourceOwnerId = null;
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(paramName)) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    resourceOwnerId = (Long) arg;
                } else if (arg instanceof String) {
                    try {
                        resourceOwnerId = Long.parseLong((String) arg);
                    } catch (NumberFormatException e) {
                        throw new AccessDeniedException("Invalid resource owner ID format");
                    }
                }
                break;
            }
        }

        if (resourceOwnerId == null) {
            throw new AccessDeniedException("Resource owner ID not found in method parameters");
        }

        permissionService.verifyResourceOwner(resourceOwnerId);
    }
}
