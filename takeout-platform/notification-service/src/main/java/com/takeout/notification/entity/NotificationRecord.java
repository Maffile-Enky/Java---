package com.takeout.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知记录表实体
 * 记录每一条推送通知的发送状态，用于消息审计和重试
 */
@Data
@TableName("t_notification_record")
public class NotificationRecord {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 通知流水号（系统生成） */
    private String notificationNo;

    /** 接收用户ID */
    private Long userId;

    /** 关联订单号 */
    private String orderNo;

    /**
     * 通知类型：
     * PAYMENT_SUCCESS-支付成功通知
     * ORDER_CANCEL-订单取消通知
     * ORDER_STATUS-订单状态变更通知
     */
    private String type;

    /**
     * 通知渠道：
     * WEBSOCKET-实时推送
     * SMS-短信
     * APP-APP推送
     */
    private String channel;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 扩展数据（JSON格式，存储附加信息） */
    private String extraData;

    /**
     * 发送状态：
     * PENDING-待发送
     * SENDING-发送中
     * SUCCESS-发送成功
     * FAILED-发送失败
     * RETRY-重试中
     */
    private String status;

    /** 重试次数 */
    private Integer retryCount;

    /** 最大重试次数 */
    private Integer maxRetry;

    /** 最后发送时间 */
    private LocalDateTime lastSendTime;

    /** 发送失败原因 */
    private String failReason;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
