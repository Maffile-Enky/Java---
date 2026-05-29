package com.takeout.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手信息表实体
 */
@Data
@TableName("t_rider")
public class Rider {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID */
    private Long userId;

    /** 骑手姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 身份证号 */
    private String idCardNo;

    /**
     * 车辆类型：
     * BIKE-自行车
     * ELECTRIC_BIKE-电动车
     * MOTORCYCLE-摩托车
     */
    private String vehicleType;

    /**
     * 骑手状态：
     * OFFLINE-离线
     * ONLINE-在线（可接单）
     * BUSY-忙碌（配送中）
     */
    private String status;

    /** 评分 */
    private BigDecimal rating;

    /** 累计完成订单数 */
    private Integer totalOrders;

    /** 当前经度 */
    private BigDecimal longitude;

    /** 当前纬度 */
    private BigDecimal latitude;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
