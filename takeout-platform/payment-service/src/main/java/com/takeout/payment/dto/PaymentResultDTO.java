package com.takeout.payment.dto;

import lombok.Data;

/**
 * 支付结果响应DTO
 * 返回给前端的支付信息（如二维码链接等）
 */
@Data
public class PaymentResultDTO {

    /** 支付流水号 */
    private String paymentNo;

    /** 支付状态 */
    private String status;

    /**
     * 支付跳转信息：
     * - 支付宝扫码：返回 qr_code 二维码链接
     * - 微信JSAPI：返回 prepay_id 用于前端调起支付
     * - H5：返回 mweb_url 跳转链接
     */
    private String payInfo;

    /** 过期时间（秒） */
    private long expireSeconds;
}
