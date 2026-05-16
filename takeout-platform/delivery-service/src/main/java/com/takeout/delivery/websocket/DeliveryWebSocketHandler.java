package com.takeout.delivery.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配送实时追踪 WebSocket 处理器
 * 管理订单追踪会话，提供骑手位置实时推送能力
 *
 * 前端连接格式：ws://host:port/ws/delivery/track/{orderNo}
 * 同一订单可有多个追踪会话（用户、商家等）
 */
@Slf4j
@Component
public class DeliveryWebSocketHandler extends TextWebSocketHandler {

    /**
     * 订单追踪会话映射表
     * key: orderNo（从 URL 路径中解析）
     * value: 该订单的所有追踪会话集合
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private final Map<String, Set<WebSocketSession>> orderSessions = new ConcurrentHashMap<>();

    /** JSON 序列化工具 */
    private final ObjectMapper objectMapper;

    public DeliveryWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * WebSocket 连接建立时触发
     * 从 URL 路径中提取 orderNo，建立订单与会话的映射
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String orderNo = extractOrderNo(session);
        if (orderNo != null) {
            orderSessions.computeIfAbsent(orderNo, k -> ConcurrentHashMap.newKeySet()).add(session);
            log.info("WebSocket 连接建立, orderNo: {}, sessionId: {}", orderNo, session.getId());
        } else {
            log.warn("WebSocket 连接缺少 orderNo, sessionId: {}", session.getId());
        }
    }

    /**
     * WebSocket 连接关闭时触发
     * 清理订单与会话的映射
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String orderNo = extractOrderNo(session);
        if (orderNo != null) {
            Set<WebSocketSession> sessions = orderSessions.get(orderNo);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    orderSessions.remove(orderNo);
                }
            }
            log.info("WebSocket 连接关闭, orderNo: {}, status: {}", orderNo, status);
        }
    }

    /**
     * 处理客户端发送的消息（心跳检测等）
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if ("ping".equals(payload)) {
            try {
                session.sendMessage(new TextMessage("pong"));
            } catch (IOException e) {
                log.warn("发送 pong 失败, sessionId: {}", session.getId(), e);
            }
        }
    }

    /**
     * 推送骑手位置信息给订单的所有追踪者
     *
     * @param orderNo  订单号
     * @param location 位置数据对象（会被序列化为JSON）
     */
    public void pushLocationToSubscribers(String orderNo, Object location) {
        Set<WebSocketSession> sessions = orderSessions.get(orderNo);
        if (sessions == null || sessions.isEmpty()) {
            log.debug("订单 {} 无追踪会话", orderNo);
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(location);
            TextMessage textMessage = new TextMessage(json);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        synchronized (session) {
                            session.sendMessage(textMessage);
                        }
                    } catch (IOException e) {
                        log.error("WebSocket 推送失败, orderNo: {}, sessionId: {}", orderNo, session.getId(), e);
                        sessions.remove(session);
                    }
                } else {
                    sessions.remove(session);
                }
            }

            log.info("WebSocket 位置推送完成, orderNo: {}, 订阅数: {}", orderNo, sessions.size());
        } catch (Exception e) {
            log.error("序列化位置数据失败, orderNo: {}", orderNo, e);
        }
    }

    /**
     * 获取订单的追踪会话数
     */
    public int getSubscriberCount(String orderNo) {
        Set<WebSocketSession> sessions = orderSessions.get(orderNo);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 从 WebSocket 会话 URL 中提取 orderNo
     * URL 格式：/ws/delivery/track/{orderNo}
     */
    private String extractOrderNo(WebSocketSession session) {
        try {
            String path = session.getUri().getPath();
            String[] segments = path.split("/");
            if (segments.length >= 5) {
                return segments[segments.length - 1];
            }
        } catch (Exception e) {
            log.warn("解析 orderNo 失败, uri: {}", session.getUri(), e);
        }
        return null;
    }
}
