package com.takeout.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 调度结果DTO
 * 返回配送任务分配结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 配送任务编号 */
    private String taskNo;

    /** 骑手ID */
    private Long riderId;

    /** 骑手姓名 */
    private String riderName;

    /** 骑手手机号 */
    private String riderPhone;

    /** 预估距离（公里） */
    private BigDecimal estimatedDistance;

    /** 预估时间（分钟） */
    private Integer estimatedTime;

    /** 配送状态 */
    private String status;
}
