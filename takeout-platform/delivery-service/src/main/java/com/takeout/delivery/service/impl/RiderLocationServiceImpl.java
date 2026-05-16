package com.takeout.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.delivery.entity.RiderLocation;
import com.takeout.delivery.mapper.RiderLocationMapper;
import com.takeout.delivery.service.RiderLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 骑手位置服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiderLocationServiceImpl extends ServiceImpl<RiderLocationMapper, RiderLocation>
        implements RiderLocationService {

    @Override
    public void recordLocation(RiderLocation location) {
        save(location);
        log.debug("骑手位置记录, riderId: {}, lon: {}, lat: {}",
                location.getRiderId(), location.getLongitude(), location.getLatitude());
    }

    @Override
    public List<RiderLocation> getRider轨迹(Long riderId, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<RiderLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiderLocation::getRiderId, riderId)
                .ge(start != null, RiderLocation::getCreatedAt, start)
                .le(end != null, RiderLocation::getCreatedAt, end)
                .orderByAsc(RiderLocation::getCreatedAt);
        return list(wrapper);
    }
}
