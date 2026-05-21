package com.takeout.delivery.service;

import java.math.BigDecimal;

/**
 * 智能调度服务接口
 * 负责骑手调度算法和距离计算
 */
public interface DispatchService {

    /**
     * 查找最近的可用骑手
     * 使用 Redis ZSet + Haversine 距离计算
     *
     * @param merchantLongitude 商家经度
     * @param merchantLatitude  商家纬度
     * @return 最近骑手的ID，无可用骑手返回null
     */
    Long findNearestRider(BigDecimal merchantLongitude, BigDecimal merchantLatitude);

    /**
     * 使用 Haversine 公式计算两点之间的距离（公里）
     *
     * @param lon1 经度1
     * @param lat1 纬度1
     * @param lon2 经度2
     * @param lat2 纬度2
     * @return 距离（公里）
     */
    BigDecimal calculateDistance(BigDecimal lon1, BigDecimal lat1, BigDecimal lon2, BigDecimal lat2);

    /**
     * 预估配送时间（分钟）
     * 基于距离和平均配送速度计算
     *
     * @param distanceKm 距离（公里）
     * @return 预估时间（分钟）
     */
    Integer estimateDeliveryTime(BigDecimal distanceKm);
}
