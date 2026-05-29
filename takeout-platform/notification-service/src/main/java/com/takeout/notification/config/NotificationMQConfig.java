package com.takeout.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通知服务 RabbitMQ 配置
 * 定义通知相关的交换机、队列、死信队列和重试机制
 *
 * 消息流转：支付成功事件 -> payment.success.notify.queue -> 通知服务消费
 * 消费失败 -> 重试3次 -> 进入死信队列 -> 人工处理
 */
@Slf4j
@Configuration
public class NotificationMQConfig {

    // ==================== 交换机 ====================

    /** 支付事件交换机（与 payment-service 共享） */
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    // ==================== 队列 ====================

    /** 支付成功 -> 通知服务队列 */
    public static final String PAYMENT_SUCCESS_NOTIFY_QUEUE = "payment.success.notify.queue";

    // ==================== 路由键 ====================

    /** 支付成功路由键 */
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";

    // ==================== 死信相关 ====================

    /** 通知死信交换机 */
    public static final String NOTIFICATION_DLX_EXCHANGE = "notification.dlx.exchange";

    /** 通知重试队列（消费失败后进入重试队列，延迟后重新消费） */
    public static final String NOTIFICATION_RETRY_QUEUE = "notification.retry.queue";

    /** 通知死信队列（重试耗尽后进入死信队列） */
    public static final String NOTIFICATION_DLQ_QUEUE = "notification.dlq.queue";

    /** 重试路由键 */
    public static final String NOTIFICATION_RETRY_ROUTING_KEY = "notification.retry";

    /** 死信路由键 */
    public static final String NOTIFICATION_DLQ_ROUTING_KEY = "notification.dlq";

    // ==================== 重试配置 ====================

    /** 重试延迟时间（毫秒） */
    private static final int RETRY_DELAY_MS = 5000;

    /**
     * 支付成功 -> 通知服务队列
     * 绑定到通知服务的死信交换机，消费失败后走重试/死信流程
     */
    @Bean
    public Queue paymentSuccessNotifyQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_NOTIFY_QUEUE)
                .deadLetterExchange(NOTIFICATION_DLX_EXCHANGE)
                .deadLetterRoutingKey(NOTIFICATION_RETRY_ROUTING_KEY)
                .build();
    }

    /**
     * 支付成功队列绑定到支付事件交换机
     */
    @Bean
    public Binding paymentSuccessNotifyBinding() {
        return BindingBuilder.bind(paymentSuccessNotifyQueue())
                .to(new TopicExchange(PAYMENT_EXCHANGE))
                .with(PAYMENT_SUCCESS_ROUTING_KEY);
    }

    // ==================== 死信交换机和队列 ====================

    /**
     * 通知死信交换机（Topic 类型）
     */
    @Bean
    public Exchange notificationDlxExchange() {
        return ExchangeBuilder.topicExchange(NOTIFICATION_DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 通知重试队列
     * 消息延迟 5 秒后重新投递到支付成功通知队列
     * 达到最大重试次数后进入死信队列
     */
    @Bean
    public Queue notificationRetryQueue() {
        return QueueBuilder.durable(NOTIFICATION_RETRY_QUEUE)
                .deadLetterExchange(NOTIFICATION_DLX_EXCHANGE)
                .deadLetterRoutingKey(NOTIFICATION_DLQ_ROUTING_KEY)
                .ttl(RETRY_DELAY_MS)
                .build();
    }

    /**
     * 通知死信队列（最终兜底，人工处理）
     */
    @Bean
    public Queue notificationDlqQueue() {
        return QueueBuilder.durable(NOTIFICATION_DLQ_QUEUE).build();
    }

    /**
     * 重试队列绑定死信交换机
     */
    @Bean
    public Binding notificationRetryBinding() {
        return BindingBuilder.bind(notificationRetryQueue())
                .to(notificationDlxExchange())
                .with(NOTIFICATION_RETRY_ROUTING_KEY)
                .noargs();
    }

    /**
     * 死信队列绑定死信交换机
     */
    @Bean
    public Binding notificationDlqBinding() {
        return BindingBuilder.bind(notificationDlqQueue())
                .to(notificationDlxExchange())
                .with(NOTIFICATION_DLQ_ROUTING_KEY)
                .noargs();
    }

    // ==================== 消息转换器 ====================

    /**
     * Jackson 消息转换器
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    /**
     * 消息监听容器工厂配置
     * 设置手动确认模式和并发消费者数量
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        return factory;
    }
}
