package com.takeout.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.delivery.entity.Rider;
import com.takeout.delivery.mapper.RiderMapper;
import com.takeout.delivery.service.RiderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 骑手服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiderServiceImpl extends ServiceImpl<RiderMapper, Rider> implements RiderService {

    private final StringRedisTemplate stringRedisTemplate;

    /** Redis 骑手位置 ZSet key */
    private static final String RIDER_LOCATION_ZSET_KEY = "delivery:rider:locations";

    @Override
    @Transactional
    public Rider registerRider(Long userId, Rider rider) {
        // 检查是否已注册
        Rider existing = getRiderByUserId(userId);
        if (existing != null) {
            throw new BusinessException(400, "您已注册为骑手");
        }

        rider.setUserId(userId);
        rider.setStatus("OFFLINE");
        rider.setRating(new BigDecimal("5.00"));
        rider.setTotalOrders(0);
        save(rider);
        log.info("骑手注册成功, userId: {}, riderId: {}", userId, rider.getId());
        return rider;
    }

    @Override
    public Rider getRiderByUserId(Long userId) {
        LambdaQueryWrapper<Rider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rider::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    @Transactional
    public Rider updateRiderStatus(Long riderId, String status) {
        Rider rider = getById(riderId);
        if (rider == null) {
            throw new BusinessException(404, "骑手不存在");
        }

        rider.setStatus(status);
        updateById(rider);

        // 更新 Redis 中的骑手位置信息（在线时添加，离线时移除）
        if ("ONLINE".equals(status) && rider.getLongitude() != null && rider.getLatitude() != null) {
            updateRiderLocationInRedis(riderId, rider.getLongitude(), rider.getLatitude());
        } else if ("OFFLINE".equals(status)) {
            stringRedisTemplate.opsForZSet().remove(RIDER_LOCATION_ZSET_KEY, String.valueOf(riderId));
        }

        log.info("骑手状态更新, riderId: {}, status: {}", riderId, status);
        return rider;
    }

    @Override
    @Transactional
    public void updateRiderLocation(Long riderId, BigDecimal longitude, BigDecimal latitude) {
        Rider rider = getById(riderId);
        if (rider == null) {
            throw new BusinessException(404, "骑手不存在");
        }

        rider.setLongitude(longitude);
        rider.setLatitude(latitude);
        updateById(rider);

        // 如果骑手在线，同步更新 Redis
        if ("ONLINE".equals(rider.getStatus()) || "BUSY".equals(rider.getStatus())) {
            updateRiderLocationInRedis(riderId, longitude, latitude);
        }
    }

    @Override
    public Rider getRiderById(Long riderId) {
        Rider rider = getById(riderId);
        if (rider == null) {
            throw new BusinessException(404, "骑手不存在");
        }
        return rider;
    }

    /**
     * 更新 Redis ZSet 中的骑手位置
     * 使用 geohash 作为 score，支持范围查询
     */
    private void updateRiderLocationInRedis(Long riderId, BigDecimal longitude, BigDecimal latitude) {
        try {
            // 使用 geohash 计算 score（longitude + latitude 的简单编码）
            // 实际项目中可以使用 Redis GEO 命令
            double score = longitude.doubleValue() * 1000 + latitude.doubleValue();
            stringRedisTemplate.opsForZSet().add(RIDER_LOCATION_ZSET_KEY, String.valueOf(riderId), score);
        } catch (Exception e) {
            log.warn("更新骑手位置到 Redis 失败, riderId: {}", riderId, e);
        }
    }
}
