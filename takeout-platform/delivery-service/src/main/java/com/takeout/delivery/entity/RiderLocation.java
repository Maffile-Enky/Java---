package com.takeout.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置记录表实体
 * 记录骑手实时位置，用于轨迹回放和配送追踪
 */
@Data
@TableName("t_rider_location")
public class RiderLocation {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 骑手ID */
    private Long riderId;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** 速度（公里/小时） */
    private BigDecimal speed;

    /** 航向（角度 0-360） */
    private Integer heading;

    /** 关联配送任务ID */
    private Long taskId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
