package com.takeout.merchant.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NearbyMerchantDTO implements Serializable {

    private Long id;

    private String name;

    private String address;

    private Double longitude;

    private Double latitude;

    private Double distance;
}