package com.takeout.common.web.util;

import com.takeout.common.core.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility to extract user info from request headers set by gateway.
 */
public class SecurityUtil {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_TRACE_ID = "X-Trace-Id";

    public static Long getUserId(HttpServletRequest request) {
        String val = request.getHeader(HEADER_USER_ID);
        if (val == null) {
            throw new BusinessException(401, "未登录");
        }
        return Long.parseLong(val);
    }

    public static Long getUserIdOrNull(HttpServletRequest request) {
        String val = request.getHeader(HEADER_USER_ID);
        return val != null ? Long.parseLong(val) : null;
    }

    public static String getUsername(HttpServletRequest request) {
        return request.getHeader(HEADER_USERNAME);
    }

    public static String getUserRole(HttpServletRequest request) {
        return request.getHeader(HEADER_USER_ROLE);
    }

    public static String getTraceId(HttpServletRequest request) {
        return request.getHeader(HEADER_TRACE_ID);
    }
}
