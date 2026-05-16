package com.takeout.search.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("search_dish")
public class SearchDish {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dishId;

    private Long merchantId;

    private String merchantName;

    private String name;

    private BigDecimal price;

    private String description;

    private String category;

    private String imageUrl;

    private Integer sales;

    private BigDecimal rating;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
