package com.takeout.delivery.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 配送服务 WebSocket 配置类
 * 注册 WebSocket 端点，用于实时配送位置追踪
 */
@Slf4j
@Configuration
@EnableWebSocket
public class DeliveryWebSocketConfig implements WebSocketConfigurer {

    private final DeliveryWebSocketHandler deliveryWebSocketHandler;

    public DeliveryWebSocketConfig(DeliveryWebSocketHandler deliveryWebSocketHandler) {
        this.deliveryWebSocketHandler = deliveryWebSocketHandler;
    }

    /**
     * 注册 WebSocket 处理器
     * 前端连接地址：ws://host:port/ws/delivery/track/{orderNo}
     * orderNo 用于标识不同订单的配送追踪会话
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deliveryWebSocketHandler, "/ws/delivery/track/{orderNo}")
                .setAllowedOrigins("*");
        log.info("WebSocket 处理器注册成功, 端点: /ws/delivery/track/{orderNo}");
    }
}
