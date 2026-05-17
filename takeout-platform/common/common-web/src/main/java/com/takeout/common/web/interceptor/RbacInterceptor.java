package com.takeout.common.web.interceptor;

import com.takeout.common.core.annotation.RequirePermission;
import com.takeout.common.core.annotation.RequireRole;
import com.takeout.common.core.enums.RoleEnum;
import com.takeout.common.core.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * RBAC interceptor that checks @RequireRole and @RequirePermission annotations.
 * Reads user role from X-User-Role header (set by gateway).
 */
public class RbacInterceptor implements HandlerInterceptor {

    private static final String ROLE_HEADER = "X-User-Role";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // Check method-level annotation first, then class-level
        RequireRole methodRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        RequireRole classRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);

        RequireRole roleAnnotation = methodRole != null ? methodRole : classRole;
        if (roleAnnotation != null) {
            String userRole = request.getHeader(ROLE_HEADER);
            if (userRole == null) {
                throw new BusinessException(401, "未登录");
            }
            RoleEnum current = RoleEnum.of(userRole);
            if (current == null) {
                throw new BusinessException(403, "无效角色");
            }
            // ADMIN is superuser - bypass role check
            if (current == RoleEnum.ADMIN) {
                return true;
            }
            boolean allowed = false;
            for (RoleEnum required : roleAnnotation.value()) {
                if (current == required) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new BusinessException(403, "无权限访问");
            }
        }

        // Check permission annotation
        RequirePermission permAnnotation = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (permAnnotation != null) {
            // Permission check relies on X-User-Role for now; extend with DB permission table if needed
            String userRole = request.getHeader(ROLE_HEADER);
            if (userRole == null) {
                throw new BusinessException(401, "未登录");
            }
        }

        return true;
    }
}
