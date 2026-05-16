package com.takeout.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.delivery.entity.Rider;
import com.takeout.delivery.mapper.RiderMapper;
import com.takeout.delivery.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 智能调度服务实现类
 * 负责骑手调度算法和距离计算
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchService {

    private final RiderMapper riderMapper;

    /** 地球半径（公里） */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /** 电动车平均配送速度（公里/小时） */
    private static final double AVG_SPEED_KMH = 15.0;

    /** 准备时间（分钟） */
    private static final int PREPARATION_TIME_MINUTES = 5;

    /** 最大搜索半径（公里） */
    private static final double MAX_SEARCH_RADIUS_KM = 10.0;

    @Override
    public Long findNearestRider(BigDecimal merchantLongitude, BigDecimal merchantLatitude) {
        // 查询所有在线且非忙碌的骑手
        LambdaQueryWrapper<Rider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rider::getStatus, "ONLINE")
                .isNotNull(Rider::getLongitude)
                .isNotNull(Rider::getLatitude);
        List<Rider> onlineRiders = riderMapper.selectList(wrapper);

        if (onlineRiders.isEmpty()) {
            log.info("没有在线的骑手可供调度");
            return null;
        }

        // 查找最近的骑手
        Rider nearestRider = null;
        BigDecimal minDistance = BigDecimal.valueOf(Double.MAX_VALUE);

        for (Rider rider : onlineRiders) {
            BigDecimal distance = calculateDistance(
                    rider.getLongitude(),
                    rider.getLatitude(),
                    merchantLongitude,
                    merchantLatitude
            );

            if (distance.compareTo(minDistance) < 0 && distance.doubleValue() <= MAX_SEARCH_RADIUS_KM) {
                minDistance = distance;
                nearestRider = rider;
            }
        }

        if (nearestRider != null) {
            log.info("找到最近骑手, riderId: {}, distance: {}km", nearestRider.getId(),
                    minDistance.setScale(2, RoundingMode.HALF_UP));
            return nearestRider.getId();
        }

        log.info("搜索半径 {}km 内没有可用骑手", MAX_SEARCH_RADIUS_KM);
        return null;
    }

    @Override
    public BigDecimal calculateDistance(BigDecimal lon1, BigDecimal lat1, BigDecimal lon2, BigDecimal lat2) {
        // Haversine 公式计算两点之间的距离
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Integer estimateDeliveryTime(BigDecimal distanceKm) {
        // 骑手取餐时间 + 骑行时间
        double ridingTimeMinutes = (distanceKm.doubleValue() / AVG_SPEED_KMH) * 60;
        int totalTime = PREPARATION_TIME_MINUTES + (int) Math.ceil(ridingTimeMinutes);
        return Math.max(totalTime, 10); // 最少10分钟
    }
}
