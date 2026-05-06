package com.takeout.merchant.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartItemDTO implements Serializable {

    private Long dishId;

    private String dishName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;
}