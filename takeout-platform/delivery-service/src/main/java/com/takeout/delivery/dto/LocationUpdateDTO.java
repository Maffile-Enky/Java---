package com.takeout.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 骑手位置更新DTO
 * 用于骑手客户端上报实时位置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 骑手ID */
    private Long riderId;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** 速度（公里/小时） */
    private BigDecimal speed;

    /** 航向（角度 0-360） */
    private Integer heading;
}
