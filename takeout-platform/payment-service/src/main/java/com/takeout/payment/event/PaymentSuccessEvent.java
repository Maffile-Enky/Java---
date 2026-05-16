package com.takeout.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付成功事件
 * 通过 RabbitMQ 发送给订单服务和通知服务
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent implements Serializable {

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
