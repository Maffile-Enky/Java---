package com.takeout.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WebSocket 推送的骑手位置消息体
 * 发送给前端的实时配送位置数据结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketLocationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息类型 */
    private String type;

    /** 关联订单号 */
    private String orderNo;

    /** 骑手ID */
    private Long riderId;

    /** 骑手姓名 */
    private String riderName;

    /** 骑手手机号 */
    private String riderPhone;

    /** 配送状态 */
    private String status;

    /** 骑手经度 */
    private BigDecimal longitude;

    /** 骑手纬度 */
    private BigDecimal latitude;

    /** 预估剩余时间（分钟） */
    private Integer estimatedTime;

    /** 消息时间 */
    private LocalDateTime timestamp;
}
