package com.takeout.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 配送任务创建DTO
 * 用于订单服务支付成功后创建配送任务
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTaskCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 关联订单ID */
    private Long orderId;

    /** 关联订单号 */
    private String orderNo;

    /** 商家ID */
    private Long merchantId;

    /** 商家名称 */
    private String merchantName;

    /** 商家地址 */
    private String merchantAddress;

    /** 商家经度 */
    private BigDecimal merchantLongitude;

    /** 商家纬度 */
    private BigDecimal merchantLatitude;

    /** 配送地址 */
    private String deliveryAddress;

    /** 配送地址经度 */
    private BigDecimal deliveryLongitude;

    /** 配送地址纬度 */
    private BigDecimal deliveryLatitude;

    /** 收货人手机号 */
    private String deliveryPhone;

    /** 收货人姓名 */
    private String deliveryName;

    /** 配送费 */
    private BigDecimal fee;

    /** 备注 */
    private String note;
}
