package com.takeout.order.event;

import com.rabbitmq.client.Channel;
import com.takeout.common.mq.constant.MQConstants;
import com.takeout.common.mq.dto.PaymentSuccessEvent;
import com.takeout.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 支付成功事件消费者
 * 监听 payment.success.order.queue，更新订单状态为已支付
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = MQConstants.PAYMENT_SUCCESS_ORDER_QUEUE)
    public void handlePaymentSuccess(PaymentSuccessEvent event, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("收到支付成功事件, orderNo: {}, paymentNo: {}", event.getOrderNo(), event.getPaymentNo());
        try {
            orderService.updateOrderStatusToPaid(event.getOrderNo());
            channel.basicAck(deliveryTag, false);
            log.info("订单状态更新成功, orderNo: {}", event.getOrderNo());
        } catch (Exception e) {
            log.error("处理支付成功事件失败, orderNo: {}", event.getOrderNo(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
