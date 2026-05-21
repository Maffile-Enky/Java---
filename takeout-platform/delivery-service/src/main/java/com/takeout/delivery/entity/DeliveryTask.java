package com.takeout.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送任务表实体
 */
@Data
@TableName("t_delivery_task")
public class DeliveryTask {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配送任务编号（系统生成） */
    private String taskNo;

    /** 关联订单ID */
    private Long orderId;

    /** 关联订单号 */
    private String orderNo;

    /** 商家ID */
    private Long merchantId;

    /** 商家名称 */
    private String merchantName;

    /** 商家地址 */
    private String merchantAddress;

    /** 商家经度 */
    private BigDecimal merchantLongitude;

    /** 商家纬度 */
    private BigDecimal merchantLatitude;

    /** 配送地址 */
    private String deliveryAddress;

    /** 配送地址经度 */
    private BigDecimal deliveryLongitude;

    /** 配送地址纬度 */
    private BigDecimal deliveryLatitude;

    /** 收货人手机号 */
    private String deliveryPhone;

    /** 收货人姓名 */
    private String deliveryName;

    /** 骑手ID */
    private Long riderId;

    /** 骑手姓名 */
    private String riderName;

    /** 骑手手机号 */
    private String riderPhone;

    /**
     * 配送状态：
     * PENDING-待分配
     * ASSIGNED-已分配
     * PICKED_UP-已取餐
     * DELIVERING-配送中
     * COMPLETED-已完成
     * CANCELLED-已取消
     */
    private String status;

    /** 预估距离（公里） */
    private BigDecimal estimatedDistance;

    /** 预估时间（分钟） */
    private Integer estimatedTime;

    /** 实际距离（公里） */
    private BigDecimal actualDistance;

    /** 实际时间（分钟） */
    private Integer actualTime;

    /** 配送费 */
    private BigDecimal fee;

    /** 备注 */
    private String note;

    /** 分配时间 */
    private LocalDateTime assignedAt;

    /** 取餐时间 */
    private LocalDateTime pickedUpAt;

    /** 送达时间 */
    private LocalDateTime deliveredAt;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
