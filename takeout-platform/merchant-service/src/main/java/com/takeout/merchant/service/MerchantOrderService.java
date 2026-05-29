package com.takeout.merchant.service;

public interface MerchantOrderService {

    /**
     * 商家接单
     * @param orderId 订单ID
     * @param merchantId 商家ID
     */
    void acceptOrder(String orderId, Long merchantId);

    /**
     * 商家出餐
     * @param orderId 订单ID
     * @param merchantId 商家ID
     */
    void readyOrder(String orderId, Long merchantId);
}