package com.takeout.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.notification.config.NotificationMQConfig;
import com.takeout.notification.dto.WebSocketMessage;
import com.takeout.notification.entity.NotificationRecord;
import com.takeout.notification.mapper.NotificationRecordMapper;
import com.takeout.notification.service.NotificationService;
import com.takeout.notification.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 通知服务实现类
 * 核心职责：创建通知记录、WebSocket 推送、失败重试
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationRecordMapper, NotificationRecord>
        implements NotificationService {

    private final NotificationRecordMapper notificationRecordMapper;
    private final NotificationWebSocketHandler webSocketHandler;
    private final RabbitTemplate rabbitTemplate;

    /** 最大重试次数 */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 处理支付成功事件
     * 创建支付成功通知 + WebSocket 实时推送
     */
    @Override
    @Transactional
    public void handlePaymentSuccess(String paymentNo, String orderNo, Long userId,
                                      String amount, String payChannel) {
        log.info("处理支付成功通知, paymentNo: {}, orderNo: {}, userId: {}", paymentNo, orderNo, userId);

        // 构造通知记录
        NotificationRecord record = new NotificationRecord();
        record.setNotificationNo(generateNotificationNo());
        record.setUserId(userId);
        record.setOrderNo(orderNo);
        record.setType("PAYMENT_SUCCESS");
        record.setChannel("WEBSOCKET");
        record.setTitle("支付成功");
        record.setContent(String.format("您的订单 %s 支付成功，金额：%s 元，支付方式：%s",
                orderNo, amount, "ALIPAY".equals(payChannel) ? "支付宝" : "微信支付"));
        record.setExtraData("{\"paymentNo\":\"" + paymentNo + "\",\"amount\":\"" + amount + "\"}");
        record.setStatus("PENDING");
        record.setRetryCount(0);
        record.setMaxRetry(MAX_RETRY_COUNT);

        // 发送通知
        sendNotification(record);
    }

    /**
     * 处理订单取消事件
     */
    @Override
    @Transactional
    public void handleOrderCancel(String orderNo, Long userId, String reason) {
        log.info("处理订单取消通知, orderNo: {}, userId: {}", orderNo, userId);

        NotificationRecord record = new NotificationRecord();
        record.setNotificationNo(generateNotificationNo());
        record.setUserId(userId);
        record.setOrderNo(orderNo);
        record.setType("ORDER_CANCEL");
        record.setChannel("WEBSOCKET");
        record.setTitle("订单已取消");
        record.setContent(String.format("您的订单 %s 已取消，原因：%s", orderNo, reason));
        record.setExtraData("{\"reason\":\"" + reason + "\"}");
        record.setStatus("PENDING");
        record.setRetryCount(0);
        record.setMaxRetry(MAX_RETRY_COUNT);

        sendNotification(record);
    }

    /**
     * 发送通知（通用方法）
     * 1. 保存通知记录到数据库
     * 2. 通过 WebSocket 推送给用户
     * 3. 根据推送结果更新状态
     */
    @Override
    @Transactional
    public boolean sendNotification(NotificationRecord record) {
        // 保存通知记录
        notificationRecordMapper.insert(record);
        log.info("通知记录已保存, notificationNo: {}", record.getNotificationNo());

        // 标记为发送中
        record.setStatus("SENDING");
        record.setLastSendTime(LocalDateTime.now());

        // 构建 WebSocket 消息
        WebSocketMessage wsMessage = WebSocketMessage.builder()
                .type(record.getType())
                .title(record.getTitle())
                .content(record.getContent())
                .orderNo(record.getOrderNo())
                .timestamp(LocalDateTime.now())
                .build();

        // 通过 WebSocket 推送
        boolean pushed = webSocketHandler.pushToUser(record.getUserId(), wsMessage);

        if (pushed) {
            // 推送成功，更新状态
            record.setStatus("SUCCESS");
            notificationRecordMapper.updateById(record);
            log.info("通知推送成功, notificationNo: {}, userId: {}",
                    record.getNotificationNo(), record.getUserId());
            return true;
        } else {
            // 推送失败，标记为待重试
            record.setStatus("FAILED");
            record.setFailReason("用户不在线或 WebSocket 连接异常");
            notificationRecordMapper.updateById(record);
            log.warn("通知推送失败（用户不在线）, notificationNo: {}, userId: {}",
                    record.getNotificationNo(), record.getUserId());

            // 投递到重试队列
            requeueToRetry(record.getNotificationNo());
            return false;
        }
    }

    /**
     * 定时重试失败的通知
     * 每 30 秒扫描一次待重试的通知记录
     * 只重试未超过最大重试次数的记录
     */
    @Override
    @Scheduled(fixedDelay = 30000)
    public void retryFailedNotifications() {
        log.info("开始扫描待重试的通知记录");

        LambdaQueryWrapper<NotificationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationRecord::getStatus, "FAILED")
                .lt(NotificationRecord::getRetryCount, MAX_RETRY_COUNT);

        List<NotificationRecord> failedList = notificationRecordMapper.selectList(wrapper);

        if (failedList.isEmpty()) {
            log.info("没有待重试的通知记录");
            return;
        }

        for (NotificationRecord record : failedList) {
            try {
                // 增加重试次数
                record.setRetryCount(record.getRetryCount() + 1);
                record.setStatus("RETRY");
                record.setLastSendTime(LocalDateTime.now());
                notificationRecordMapper.updateById(record);

                log.info("重试通知推送, notificationNo: {}, 第 {} 次重试",
                        record.getNotificationNo(), record.getRetryCount());

                // 构建 WebSocket 消息并推送
                WebSocketMessage wsMessage = WebSocketMessage.builder()
                        .type(record.getType())
                        .title(record.getTitle())
                        .content(record.getContent())
                        .orderNo(record.getOrderNo())
                        .timestamp(LocalDateTime.now())
                        .build();

                boolean pushed = webSocketHandler.pushToUser(record.getUserId(), wsMessage);

                if (pushed) {
                    record.setStatus("SUCCESS");
                    notificationRecordMapper.updateById(record);
                    log.info("重试推送成功, notificationNo: {}", record.getNotificationNo());
                } else {
                    // 重试次数用尽，标记为最终失败
                    if (record.getRetryCount() >= MAX_RETRY_COUNT) {
                        record.setStatus("FAILED");
                        record.setFailReason("重试次数已耗尽（" + MAX_RETRY_COUNT + "次）");
                        notificationRecordMapper.updateById(record);
                        log.error("通知推送最终失败，重试次数耗尽, notificationNo: {}",
                                record.getNotificationNo());
                    } else {
                        record.setStatus("FAILED");
                        notificationRecordMapper.updateById(record);
                    }
                }
            } catch (Exception e) {
                log.error("重试通知推送异常, notificationNo: {}", record.getNotificationNo(), e);
            }
        }
    }

    /**
     * 查询用户的通知记录
     */
    @Override
    public IPage<NotificationRecord> listUserNotifications(Long userId, Page<NotificationRecord> page) {
        LambdaQueryWrapper<NotificationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationRecord::getUserId, userId)
                .orderByDesc(NotificationRecord::getCreatedAt);
        return notificationRecordMapper.selectPage(page, wrapper);
    }

    /**
     * 将失败的消息投递到重试队列（RabbitMQ 延迟队列）
     * 通过 MQ 的 TTL + DLX 机制实现延迟重试
     */
    @Override
    public boolean requeueToRetry(String notificationNo) {
        try {
            rabbitTemplate.convertAndSend(
                    NotificationMQConfig.NOTIFICATION_DLX_EXCHANGE,
                    NotificationMQConfig.NOTIFICATION_RETRY_ROUTING_KEY,
                    notificationNo
            );
            log.info("消息已投递到重试队列, notificationNo: {}", notificationNo);
            return true;
        } catch (Exception e) {
            log.error("投递重试队列失败, notificationNo: {}", notificationNo, e);
            return false;
        }
    }

    /**
     * 获取当前 WebSocket 在线用户数
     */
    @Override
    public int getOnlineUserCount() {
        return webSocketHandler.getOnlineCount();
    }

    /**
     * 生成通知流水号
     * 格式：NTF + 时间戳 + 6位随机数
     */
    private String generateNotificationNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "NTF" + timestamp + random;
    }
}
