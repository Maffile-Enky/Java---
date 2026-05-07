package com.takeout.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeout.common.core.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global exception handler for the reactive gateway.
 * Converts exceptions to unified JSON responses.
 */
@Slf4j
@Order(-1)
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        int code;
        String message;
        if (ex instanceof ResponseStatusException rse) {
            code = rse.getStatusCode().value();
            message = rse.getReason() != null ? rse.getReason() : "请求错误";
        } else {
            code = HttpStatus.INTERNAL_SERVER_ERROR.value();
            message = "网关内部错误";
            log.error("网关异常 [{}]: ", exchange.getRequest().getURI().getPath(), ex);
        }

        response.setStatusCode(HttpStatus.valueOf(code >= 100 && code < 600 ? code : 500));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<Void> result = Result.error(code, message);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }
}
