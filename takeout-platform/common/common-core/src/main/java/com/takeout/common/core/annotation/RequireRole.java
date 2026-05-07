package com.takeout.common.core.annotation;

import com.takeout.common.core.enums.RoleEnum;

import java.lang.annotation.*;

/**
 * Role-based access control annotation.
 * Apply to controller methods or classes to restrict access by role.
 * Checked by gateway AuthFilter or downstream RBAC interceptor.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    RoleEnum[] value();
}
