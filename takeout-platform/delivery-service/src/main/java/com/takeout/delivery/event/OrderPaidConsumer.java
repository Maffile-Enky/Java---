package com.takeout.delivery.event;

import com.rabbitmq.client.Channel;
import com.takeout.common.mq.dto.PaymentSuccessEvent;
import com.takeout.delivery.dto.DeliveryTaskCreateDTO;
import com.takeout.delivery.service.DeliveryTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 订单支付成功事件消费者
 * 监听 RabbitMQ 支付成功队列，消费消息并自动创建配送任务
 *
 * 消费流程：
 * 1. 收到支付成功消息
 * 2. 创建配送任务（PENDING状态）
 * 3. 自动调度分配骑手
 * 4. 手动确认消息（ACK）
 * 5. 消费失败时拒绝消息（NACK），进入重试/死信队列
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidConsumer {

    private final DeliveryTaskService deliveryTaskService;

    /**
     * 消费支付成功事件
     * 监听队列：payment.success.delivery.queue
     *
     * 手动确认模式：消费成功 ACK，消费失败 NACK（不重新入队，走死信流程）
     */
    @RabbitListener(queues = "payment.success.delivery.queue",
            containerFactory = "rabbitListenerContainerFactory")
    public void handlePaymentSuccess(@Payload PaymentSuccessEvent message,
                                      Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("收到支付成功事件（配送服务）, paymentNo: {}, orderNo: {}, userId: {}",
                message.getPaymentNo(), message.getOrderNo(), message.getUserId());

        try {
            // 构建配送任务创建DTO
            // 注意：商家地址、配送地址等信息需要从订单服务获取
            // 这里先使用消息中携带的信息，实际项目中可能需要调用订单服务API
            DeliveryTaskCreateDTO taskDTO = DeliveryTaskCreateDTO.builder()
                    .orderNo(message.getOrderNo())
                    .build();

            // 创建配送任务
            deliveryTaskService.createTask(taskDTO);

            // 消费成功，确认消息
            channel.basicAck(deliveryTag, false);
            log.info("配送任务创建完成, orderNo: {}", message.getOrderNo());

        } catch (Exception e) {
            log.error("处理支付成功事件异常（配送服务）, orderNo: {}", message.getOrderNo(), e);
            try {
                // 消费失败，拒绝消息（不重新入队）
                // 消息会根据队列配置进入死信交换机 -> 重试队列
                channel.basicNack(deliveryTag, false, false);
                log.info("消息已拒绝，将进入死信/重试队列, orderNo: {}", message.getOrderNo());
            } catch (IOException ioException) {
                log.error("NACK 操作失败, deliveryTag: {}", deliveryTag, ioException);
            }
        }
    }
}
