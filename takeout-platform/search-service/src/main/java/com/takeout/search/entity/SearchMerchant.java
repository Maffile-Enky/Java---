package com.takeout.search.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("search_merchant")
public class SearchMerchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private String name;

    private String address;

    private String description;

    private String imageUrl;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal rating;

    private Integer monthlySales;

    private Integer avgDeliveryTime;

    private String categories;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
