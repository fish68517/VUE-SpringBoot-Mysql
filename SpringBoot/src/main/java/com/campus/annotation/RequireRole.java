package com.campus.annotation;

import com.campus.entity.User.UserRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RequireRole Annotation
 * Used to control access to methods based on user roles
 * Can be applied to controller methods to enforce role-based access control
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    
    /**
     * Required roles to access the method
     * If multiple roles are specified, user must have at least one of them
     */
    UserRole[] value() default {};
    
}
