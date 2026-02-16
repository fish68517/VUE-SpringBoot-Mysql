package com.campus.aspect;

import com.campus.annotation.RequireRole;
import com.campus.entity.User.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Role Check Aspect
 * Intercepts methods annotated with @RequireRole and validates user permissions
 */
@Slf4j
@Aspect
@Component
public class RoleCheckAspect {

    /**
     * Check role before method execution
     */
    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to: {}", joinPoint.getSignature());
            throw new SecurityException("User is not authenticated");
        }
        
        // Get required roles
        UserRole[] requiredRoles = requireRole.value();
        
        if (requiredRoles.length == 0) {
            log.debug("No specific roles required for: {}", joinPoint.getSignature());
            return;
        }
        
        // Extract user role from authorities
        String userRole = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse(null);
        
        if (userRole == null) {
            log.warn("User has no role assigned");
            throw new SecurityException("User has no role assigned");
        }
        
        // Check if user has required role
        boolean hasRequiredRole = Arrays.stream(requiredRoles)
                .anyMatch(role -> role.name().equals(userRole));
        
        if (!hasRequiredRole) {
            log.warn("User {} attempted to access restricted resource. Required roles: {}, User role: {}", 
                    authentication.getName(), 
                    Arrays.toString(requiredRoles), 
                    userRole);
            throw new SecurityException("User does not have required role to access this resource");
        }
        
        log.debug("User {} with role {} passed role check for: {}", 
                authentication.getName(), userRole, joinPoint.getSignature());
    }

}
