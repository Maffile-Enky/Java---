package com.takeout.common.mq.constant;

/**
 * RabbitMQ 消息队列统一常量定义
 * 所有服务共用的交换机、队列、路由键名称在此定义
 * 避免各服务自行定义导致名称不一致
 */
public final class MQConstants {

    private MQConstants() {
        // 工具类，禁止实例化
    }

    // ==================== 支付相关 ====================

    /** 支付事件交换机（Topic类型） */
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    /** 支付成功 -> 订单服务队列 */
    public static final String PAYMENT_SUCCESS_ORDER_QUEUE = "payment.success.order.queue";

    /** 支付成功 -> 通知服务队列 */
    public static final String PAYMENT_SUCCESS_NOTIFY_QUEUE = "payment.success.notify.queue";

    /** 支付成功路由键 */
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";

    // ==================== 支付死信相关 ====================

    /** 支付事件死信交换机 */
    public static final String PAYMENT_DLX_EXCHANGE = "payment.dlx.exchange";

    /** 支付事件死信队列 */
    public static final String PAYMENT_DLQ_QUEUE = "payment.dlq.queue";

    /** 支付事件死信路由键 */
    public static final String PAYMENT_DLQ_ROUTING_KEY = "payment.dlq";

    // ==================== 订单相关 ====================

    /** 订单事件交换机 */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /** 订单取消路由键 */
    public static final String ORDER_CANCEL_ROUTING_KEY = "order.cancel";

    /** 订单取消 -> 通知服务队列 */
    public static final String ORDER_CANCEL_NOTIFY_QUEUE = "order.cancel.notify.queue";

    // ==================== 通知相关 ====================

    /** 通知死信交换机 */
    public static final String NOTIFICATION_DLX_EXCHANGE = "notification.dlx.exchange";

    /** 通知重试队列 */
    public static final String NOTIFICATION_RETRY_QUEUE = "notification.retry.queue";

    /** 通知死信队列 */
    public static final String NOTIFICATION_DLQ_QUEUE = "notification.dlq.queue";

    // ==================== 通知类型 ====================

    /** 通知类型：支付成功 */
    public static final String NOTIFY_TYPE_PAYMENT_SUCCESS = "PAYMENT_SUCCESS";

    /** 通知类型：订单取消 */
    public static final String NOTIFY_TYPE_ORDER_CANCEL = "ORDER_CANCEL";

    /** 通知类型：订单状态变更 */
    public static final String NOTIFY_TYPE_ORDER_STATUS = "ORDER_STATUS";
}
