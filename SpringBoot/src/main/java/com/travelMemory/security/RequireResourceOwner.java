package com.travelMemory.security;

import java.lang.annotation.*;

/**
 * Annotation to check if the current user is the owner of a resource
 * Used on controller methods to enforce ownership verification
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireResourceOwner {
    /**
     * The name of the parameter that contains the resource owner ID
     * @return parameter name
     */
    String paramName() default "userId";
}
