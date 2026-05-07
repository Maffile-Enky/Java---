package com.takeout.common.web.trace;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * MDC trace interceptor for downstream services.
 * Reads X-Trace-Id from gateway header, or generates a new one.
 * Puts it into SLF4J MDC for log correlation.
 */
public class TraceIdInterceptor implements HandlerInterceptor {

    public static final String TRACE_ID_KEY = "traceId";
    public static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(TRACE_ID_KEY, traceId);
        response.setHeader(TRACE_HEADER, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        MDC.remove(TRACE_ID_KEY);
    }
}
