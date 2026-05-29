package com.takeout.merchant.service.impl;

import com.takeout.merchant.dto.OrderEventDTO;
import com.takeout.merchant.service.MerchantOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantOrderServiceImpl implements MerchantOrderService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mq.exchange.order}")
    private String orderExchange;

    @Value("${mq.routing-key.order.accepted}")
    private String orderAcceptedRoutingKey;

    @Value("${mq.routing-key.order.ready}")
    private String orderReadyRoutingKey;

    @Override
    public void acceptOrder(String orderId, Long merchantId) {
        OrderEventDTO event = new OrderEventDTO();
        event.setOrderId(orderId);
        event.setMerchantId(merchantId);
        event.setEventType("ACCEPTED");

        rabbitTemplate.convertAndSend(orderExchange, orderAcceptedRoutingKey, event);
        log.info("商家 {} 接单事件已发送，订单ID: {}", merchantId, orderId);
    }

    @Override
    public void readyOrder(String orderId, Long merchantId) {
        OrderEventDTO event = new OrderEventDTO();
        event.setOrderId(orderId);
        event.setMerchantId(merchantId);
        event.setEventType("READY");

        rabbitTemplate.convertAndSend(orderExchange, orderReadyRoutingKey, event);
        log.info("商家 {} 出餐事件已发送，订单ID: {}", merchantId, orderId);
    }
}