package com.takeout.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long userId;
    private Long merchantId;
    private String merchantName;

    private BigDecimal totalPrice;
    private BigDecimal deliveryFee;
    private Integer totalQuantity;

    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryName;
    private String note;

    /** PENDING/PAID/CONFIRMED/DELIVERING/COMPLETED/CANCELLED */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<OrderItem> items;
}
