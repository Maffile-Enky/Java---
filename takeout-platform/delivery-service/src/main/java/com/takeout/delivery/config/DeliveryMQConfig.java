package com.takeout.delivery.config;

import com.takeout.common.mq.constant.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配送服务 RabbitMQ 配置
 * 定义配送相关的交换机、队列、死信队列和重试机制
 *
 * 消息流转：支付成功事件 -> payment.success.delivery.queue -> 配送服务消费
 * 消费失败 -> 重试3次 -> 进入死信队列 -> 人工处理
 */
@Slf4j
@Configuration
public class DeliveryMQConfig {

    // ==================== 队列 ====================

    /** 支付成功 -> 配送服务队列 */
    public static final String PAYMENT_SUCCESS_DELIVERY_QUEUE = "payment.success.delivery.queue";

    // ==================== 死信相关 ====================

    /** 配送死信交换机 */
    public static final String DELIVERY_DLX_EXCHANGE = "delivery.dlx.exchange";

    /** 配送重试队列（消费失败后进入重试队列，延迟后重新消费） */
    public static final String DELIVERY_RETRY_QUEUE = "delivery.retry.queue";

    /** 配送死信队列（重试耗尽后进入死信队列） */
    public static final String DELIVERY_DLQ_QUEUE = "delivery.dlq.queue";

    /** 重试路由键 */
    public static final String DELIVERY_RETRY_ROUTING_KEY = "delivery.retry";

    /** 死信路由键 */
    public static final String DELIVERY_DLQ_ROUTING_KEY = "delivery.dlq";

    // ==================== 重试配置 ====================

    /** 重试延迟时间（毫秒） */
    private static final int RETRY_DELAY_MS = 5000;

    /**
     * 支付成功 -> 配送服务队列
     * 绑定到配送服务的死信交换机，消费失败后走重试/死信流程
     */
    @Bean
    public Queue paymentSuccessDeliveryQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_DELIVERY_QUEUE)
                .deadLetterExchange(DELIVERY_DLX_EXCHANGE)
                .deadLetterRoutingKey(DELIVERY_RETRY_ROUTING_KEY)
                .build();
    }

    /**
     * 配送服务队列绑定到支付事件交换机
     */
    @Bean
    public Binding paymentSuccessDeliveryBinding() {
        return BindingBuilder.bind(paymentSuccessDeliveryQueue())
                .to(new TopicExchange(MQConstants.PAYMENT_EXCHANGE))
                .with(MQConstants.PAYMENT_SUCCESS_ROUTING_KEY);
    }

    // ==================== 死信交换机和队列 ====================

    /**
     * 配送死信交换机（Topic 类型）
     */
    @Bean
    public Exchange deliveryDlxExchange() {
        return ExchangeBuilder.topicExchange(DELIVERY_DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 配送重试队列
     * 消息延迟 5 秒后重新投递到配送服务队列
     * 达到最大重试次数后进入死信队列
     */
    @Bean
    public Queue deliveryRetryQueue() {
        return QueueBuilder.durable(DELIVERY_RETRY_QUEUE)
                .deadLetterExchange(DELIVERY_DLX_EXCHANGE)
                .deadLetterRoutingKey(DELIVERY_DLQ_ROUTING_KEY)
                .ttl(RETRY_DELAY_MS)
                .build();
    }

    /**
     * 配送死信队列（最终兜底，人工处理）
     */
    @Bean
    public Queue deliveryDlqQueue() {
        return QueueBuilder.durable(DELIVERY_DLQ_QUEUE).build();
    }

    /**
     * 重试队列绑定死信交换机
     */
    @Bean
    public Binding deliveryRetryBinding() {
        return BindingBuilder.bind(deliveryRetryQueue())
                .to(deliveryDlxExchange())
                .with(DELIVERY_RETRY_ROUTING_KEY)
                .noargs();
    }

    /**
     * 死信队列绑定死信交换机
     */
    @Bean
    public Binding deliveryDlqBinding() {
        return BindingBuilder.bind(deliveryDlqQueue())
                .to(deliveryDlxExchange())
                .with(DELIVERY_DLQ_ROUTING_KEY)
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
