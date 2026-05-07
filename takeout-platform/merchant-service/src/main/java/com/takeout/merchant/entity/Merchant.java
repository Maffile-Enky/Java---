package com.takeout.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String address;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String phone;

    private String description;

    private String imageUrl;

    private Integer status;

    private Integer sortWeight;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}