package com.takeout.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付成功消息DTO
 * 对应 payment-service 发送的 PaymentSuccessEvent
 * 通知服务从 RabbitMQ 消费后用于构造通知
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 支付流水号 */
    private String paymentNo;

    /** 业务订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付渠道 */
    private String payChannel;

    /** 第三方交易流水号 */
    private String tradeNo;

    /** 事件发生时间戳 */
    private Long timestamp;
}
