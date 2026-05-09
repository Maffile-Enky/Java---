package com.takeout.notification.event;

import com.rabbitmq.client.Channel;
import com.takeout.notification.dto.PaymentSuccessMessage;
import com.takeout.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 支付成功事件消费者
 * 监听 RabbitMQ 支付成功队列，消费消息并触发通知推送
 *
 * 消费流程：
 * 1. 收到支付成功消息
 * 2. 创建通知记录
 * 3. WebSocket 实时推送给用户
 * 4. 手动确认消息（ACK）
 * 5. 消费失败时拒绝消息（NACK），进入重试/死信队列
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final NotificationService notificationService;

    /**
     * 消费支付成功事件
     * 监听队列：payment.success.notify.queue
     *
     * 手动确认模式：消费成功 ACK，消费失败 NACK（不重新入队，走死信流程）
     */
    @RabbitListener(queues = "payment.success.notify.queue",
            containerFactory = "rabbitListenerContainerFactory")
    public void handlePaymentSuccess(@Payload PaymentSuccessMessage message,
                                      Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("收到支付成功事件, paymentNo: {}, orderNo: {}, userId: {}",
                message.getPaymentNo(), message.getOrderNo(), message.getUserId());

        try {
            // 调用通知服务处理支付成功事件
            notificationService.handlePaymentSuccess(
                    message.getPaymentNo(),
                    message.getOrderNo(),
                    message.getUserId(),
                    message.getAmount() != null ? message.getAmount().toPlainString() : "0",
                    message.getPayChannel()
            );

            // 消费成功，确认消息
            channel.basicAck(deliveryTag, false);
            log.info("支付成功事件处理完成, paymentNo: {}", message.getPaymentNo());

        } catch (Exception e) {
            log.error("处理支付成功事件异常, paymentNo: {}", message.getPaymentNo(), e);
            try {
                // 消费失败，拒绝消息（不重新入队）
                // 消息会根据队列配置进入死信交换机 -> 重试队列
                channel.basicNack(deliveryTag, false, false);
                log.info("消息已拒绝，将进入死信/重试队列, paymentNo: {}", message.getPaymentNo());
            } catch (IOException ioException) {
                log.error("NACK 操作失败, deliveryTag: {}", deliveryTag, ioException);
            }
        }
    }
}
