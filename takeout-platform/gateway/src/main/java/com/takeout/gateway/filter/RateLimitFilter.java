package com.takeout.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.ErrorCode;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-gateway rate limiting filter using sliding window.
 * Limits each client IP to maxRequests per windowSeconds.
 * For production, replace with Sentinel or Redis-based rate limiting.
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final Map<String, RequestCounter> counters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_SECONDS = 60;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress() != null ?
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";

        RequestCounter counter = counters.compute(clientIp, (key, existing) -> {
            long now = System.currentTimeMillis();
            if (existing == null || now - existing.windowStart > WINDOW_SECONDS * 1000) {
                return new RequestCounter(now);
            }
            return existing;
        });

        if (counter.count.incrementAndGet() > MAX_REQUESTS) {
            return handleRateLimit(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> handleRateLimit(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<Void> result = Result.error(ErrorCode.RATE_LIMIT_EXCEEDED.getCode(),
                ErrorCode.RATE_LIMIT_EXCEEDED.getMessage());
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -3; // Run before everything else
    }

    private static class RequestCounter {
        final long windowStart;
        final AtomicInteger count = new AtomicInteger(0);

        RequestCounter(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
