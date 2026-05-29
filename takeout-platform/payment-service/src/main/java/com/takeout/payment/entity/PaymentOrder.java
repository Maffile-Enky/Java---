package com.takeout.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付流水表实体
 * 记录每一笔支付的完整生命周期信息
 */
@Data
@TableName("t_payment_order")
public class PaymentOrder {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 支付流水号（系统生成，唯一） */
    private String paymentNo;

    /** 关联的业务订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付渠道：ALIPAY-支付宝 / WECHAT-微信 */
    private String payChannel;

    /** 支付类型：NATIVE-扫码 / JSAPI-公众号 / H5-手机网页 */
    private String payType;

    /**
     * 支付状态：
     * CREATED-已创建 / PENDING-待支付 / SUCCESS-支付成功
     * FAILED-支付失败 / REFUNDED-已退款 / CLOSED-已关闭
     */
    private String status;

    /** 第三方支付交易流水号 */
    private String tradeNo;

    /** 支付成功时间 */
    private LocalDateTime payTime;

    /** 支付过期时间（超过未支付自动关闭） */
    private LocalDateTime expireTime;

    /** 回调通知原始内容（用于排查问题） */
    private String callbackData;

    /** 退款原因 */
    private String refundReason;

    /** 备注信息 */
    private String remark;

    /** 乐观锁版本号（防止并发更新） */
    @Version
    private Integer version;

    /** 逻辑删除标记：0-未删除 1-已删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
