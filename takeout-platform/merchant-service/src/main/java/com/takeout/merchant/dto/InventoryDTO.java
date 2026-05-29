package com.takeout.merchant.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class InventoryDTO implements Serializable {

    private Long dishId;

    private Integer quantity;

    private String orderId;
}
