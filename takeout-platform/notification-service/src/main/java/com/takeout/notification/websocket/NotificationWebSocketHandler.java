package com.takeout.notification.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeout.notification.dto.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 通知推送处理器
 * 管理用户 WebSocket 会话，提供实时消息推送能力
 *
 * 前端连接格式：ws://host:port/ws/notification/{userId}
 * 每个用户维护一个 WebSocket 会话，支持断线重连
 */
@Slf4j
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    /**
     * 用户会话映射表
     * key: userId（从 URL 路径中解析）
     * value: WebSocket 会话
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    /** JSON 序列化工具 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * WebSocket 连接建立时触发
     * 从 URL 路径中提取 userId，建立用户与会话的映射
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = extractUserId(session);
        if (userId != null) {
            // 如果该用户已有旧连接，先关闭旧连接
            WebSocketSession oldSession = userSessions.put(userId, session);
            if (oldSession != null && oldSession.isOpen()) {
                try {
                    oldSession.close();
                } catch (IOException e) {
                    log.warn("关闭旧 WebSocket 连接失败, userId: {}", userId, e);
                }
            }
            log.info("WebSocket 连接建立, userId: {}, sessionId: {}", userId, session.getId());
        } else {
            log.warn("WebSocket 连接缺少 userId, sessionId: {}", session.getId());
        }
    }

    /**
     * WebSocket 连接关闭时触发
     * 清理用户与会话的映射
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("WebSocket 连接关闭, userId: {}, status: {}", userId, status);
        }
    }

    /**
     * 处理客户端发送的消息（心跳检测等）
     * 当前主要用途：客户端发送 "ping"，服务端回复 "pong"
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
     * 推送通知给指定用户
     * 如果用户在线（有活跃 WebSocket 连接），直接推送
     * 如果用户离线，返回 false，由调用方决定是否需要其他通知方式
     *
     * @param userId 用户ID
     * @param message 通知消息
     * @return 是否推送成功
     */
    public boolean pushToUser(Long userId, WebSocketMessage message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                synchronized (session) {
                    session.sendMessage(new TextMessage(json));
                }
                log.info("WebSocket 推送成功, userId: {}, type: {}", userId, message.getType());
                return true;
            } catch (IOException e) {
                log.error("WebSocket 推送失败, userId: {}", userId, e);
                userSessions.remove(userId);
                return false;
            }
        }
        log.info("用户不在线, userId: {}", userId);
        return false;
    }

    /**
     * 获取当前在线用户数
     */
    public int getOnlineCount() {
        return userSessions.size();
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 从 WebSocket 会话 URL 中提取 userId
     * URL 格式：/ws/notification/{userId}
     */
    private Long extractUserId(WebSocketSession session) {
        try {
            String path = session.getUri().getPath();
            String[] segments = path.split("/");
            if (segments.length >= 4) {
                return Long.parseLong(segments[segments.length - 1]);
            }
        } catch (Exception e) {
            log.warn("解析 userId 失败, uri: {}", session.getUri(), e);
        }
        return null;
    }
}
