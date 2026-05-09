package com.takeout.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建支付订单请求DTO
 * 前端发起支付时传入的参数
 */
@Data
public class PaymentCreateDTO {

    /** 业务订单号 */
    private String orderNo;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付渠道：ALIPAY / WECHAT */
    private String payChannel;

    /** 支付类型：NATIVE / JSAPI / H5 */
    private String payType;

    /** 商品描述（支付页面展示） */
    private String subject;
}
