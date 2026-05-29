package com.takeout.merchant.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderEventDTO implements Serializable {

    private String orderId;
    
    private Long merchantId;
    
    private String eventType; // ACCEPTED, PREPARING, READY
}