package com.takeout.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket 推送消息体
 * 发送给前端的实时通知数据结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    /** 消息类型（对应通知类型） */
    private String type;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 关联订单号 */
    private String orderNo;

    /** 扩展数据 */
    private Object extraData;

    /** 消息时间 */
    private LocalDateTime timestamp;
}
