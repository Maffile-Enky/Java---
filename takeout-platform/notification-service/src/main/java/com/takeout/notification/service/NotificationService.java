package com.takeout.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.notification.entity.NotificationRecord;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<NotificationRecord> {

    /**
     * 处理支付成功事件
     * 创建通知记录 + WebSocket 实时推送
     *
     * @param paymentNo 支付流水号
     * @param orderNo 业务订单号
     * @param userId 用户ID
     * @param amount 支付金额
     * @param payChannel 支付渠道
     */
    void handlePaymentSuccess(String paymentNo, String orderNo, Long userId,
                               String amount, String payChannel);

    /**
     * 处理订单取消事件
     *
     * @param orderNo 业务订单号
     * @param userId 用户ID
     * @param reason 取消原因
     */
    void handleOrderCancel(String orderNo, Long userId, String reason);

    /**
     * 发送通知（通用）
     * 1. 保存通知记录到数据库
     * 2. 通过 WebSocket 推送
     * 3. 更新发送状态
     *
     * @param record 通知记录
     * @return 是否发送成功
     */
    boolean sendNotification(NotificationRecord record);

    /**
     * 重试发送失败的通知
     * 定时任务调用，扫描重试次数未耗尽的失败记录
     */
    void retryFailedNotifications();

    /**
     * 查询用户的通知记录
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 通知记录分页
     */
    IPage<NotificationRecord> listUserNotifications(Long userId, Page<NotificationRecord> page);

    /**
     * 将失败的消息重新投递到重试队列
     *
     * @param notificationNo 通知流水号
     * @return 是否投递成功
     */
    boolean requeueToRetry(String notificationNo);

    /**
     * 获取当前 WebSocket 在线用户数
     */
    int getOnlineUserCount();
}
