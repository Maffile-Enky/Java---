package com.takeout.common.core.annotation;

import java.lang.annotation.*;

/**
 * Permission-based access control annotation.
 * Apply to controller methods to restrict by specific permission strings.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    String[] value();
}
