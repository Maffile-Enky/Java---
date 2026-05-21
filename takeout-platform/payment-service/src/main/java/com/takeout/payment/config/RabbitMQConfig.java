package com.takeout.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付服务 RabbitMQ 配置
 * 定义支付相关的交换机、队列和绑定关系
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    // ==================== 交换机 ====================

    /** 支付事件交换机（Topic类型） */
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    // ==================== 队列 ====================

    /** 支付成功队列（订单服务消费） */
    public static final String PAYMENT_SUCCESS_ORDER_QUEUE = "payment.success.order.queue";

    /** 支付成功通知队列（通知服务消费） */
    public static final String PAYMENT_SUCCESS_NOTIFY_QUEUE = "payment.success.notify.queue";

    // ==================== 路由键 ====================

    /** 支付成功路由键 */
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";

    // ==================== 死信 ====================

    /** 支付事件死信交换机 */
    public static final String PAYMENT_DLX_EXCHANGE = "payment.dlx.exchange";

    /** 支付事件死信队列 */
    public static final String PAYMENT_DLQ_QUEUE = "payment.dlq.queue";

    /**
     * 支付事件交换机（Topic）
     * 用于发布支付相关事件，路由到不同的消费者队列
     */
    @Bean
    public Exchange paymentExchange() {
        return ExchangeBuilder.topicExchange(PAYMENT_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 支付成功 -> 订单服务队列
     * 绑定死信交换机，消费失败后进入死信队列
     */
    @Bean
    public Queue paymentSuccessOrderQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_ORDER_QUEUE)
                .deadLetterExchange(PAYMENT_DLX_EXCHANGE)
                .deadLetterRoutingKey("payment.dlq")
                .build();
    }

    /**
     * 支付成功 -> 通知服务队列
     * 绑定死信交换机，消费失败后进入死信队列
     */
    @Bean
    public Queue paymentSuccessNotifyQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_NOTIFY_QUEUE)
                .deadLetterExchange(PAYMENT_DLX_EXCHANGE)
                .deadLetterRoutingKey("payment.dlq")
                .build();
    }

    /**
     * 订单服务队列绑定到支付事件交换机
     */
    @Bean
    public Binding paymentSuccessOrderBinding() {
        return BindingBuilder.bind(paymentSuccessOrderQueue())
                .to(paymentExchange())
                .with(PAYMENT_SUCCESS_ROUTING_KEY)
                .noargs();
    }

    /**
     * 通知服务队列绑定到支付事件交换机
     */
    @Bean
    public Binding paymentSuccessNotifyBinding() {
        return BindingBuilder.bind(paymentSuccessNotifyQueue())
                .to(paymentExchange())
                .with(PAYMENT_SUCCESS_ROUTING_KEY)
                .noargs();
    }

    /**
     * 死信交换机
     */
    @Bean
    public Exchange paymentDlxExchange() {
        return ExchangeBuilder.topicExchange(PAYMENT_DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 死信队列（用于存储消费失败的消息，后续人工处理或重试）
     */
    @Bean
    public Queue paymentDlqQueue() {
        return QueueBuilder.durable(PAYMENT_DLQ_QUEUE).build();
    }

    /**
     * 死信队列绑定死信交换机
     */
    @Bean
    public Binding paymentDlqBinding() {
        return BindingBuilder.bind(paymentDlqQueue())
                .to(paymentDlxExchange())
                .with("payment.dlq")
                .noargs();
    }

    /**
     * 消息转换器：使用 Jackson 将消息序列化为 JSON
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置：使用 JSON 转换器 + 发送确认回调
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        // 消息发送到交换机后的确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送到交换机失败，correlationData: {}, 原因: {}", correlationData, cause);
            }
        });
        // 消息路由不到队列的回调
        template.setReturnsCallback(returned -> {
            log.error("消息路由到队列失败，routingKey: {}, 原因: {}", returned.getRoutingKey(), returned.getReplyText());
        });
        return template;
    }
}
