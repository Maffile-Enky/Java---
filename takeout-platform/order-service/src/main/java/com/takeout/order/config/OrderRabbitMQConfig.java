package com.takeout.order.config;

import com.takeout.common.mq.constant.MQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitMQConfig {

    // ==================== 支付成功事件（订单服务消费） ====================

    @Bean
    public Queue paymentSuccessOrderQueue() {
        return QueueBuilder.durable(MQConstants.PAYMENT_SUCCESS_ORDER_QUEUE)
                .deadLetterExchange(MQConstants.PAYMENT_DLX_EXCHANGE)
                .deadLetterRoutingKey(MQConstants.PAYMENT_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding paymentSuccessOrderBinding(Queue paymentSuccessOrderQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentSuccessOrderQueue)
                .to(paymentExchange)
                .with(MQConstants.PAYMENT_SUCCESS_ROUTING_KEY);
    }

    // ==================== 交换机 ====================

    @Bean
    public TopicExchange paymentExchange() {
        return ExchangeBuilder.topicExchange(MQConstants.PAYMENT_EXCHANGE)
                .durable(true)
                .build();
    }

    // ==================== 死信 ====================

    @Bean
    public TopicExchange paymentDlxExchange() {
        return ExchangeBuilder.topicExchange(MQConstants.PAYMENT_DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue paymentDlqQueue() {
        return QueueBuilder.durable(MQConstants.PAYMENT_DLQ_QUEUE).build();
    }

    @Bean
    public Binding paymentDlqBinding(Queue paymentDlqQueue, TopicExchange paymentDlxExchange) {
        return BindingBuilder.bind(paymentDlqQueue)
                .to(paymentDlxExchange)
                .with(MQConstants.PAYMENT_DLQ_ROUTING_KEY);
    }

    // ==================== 序列化 ====================

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        return factory;
    }
}
