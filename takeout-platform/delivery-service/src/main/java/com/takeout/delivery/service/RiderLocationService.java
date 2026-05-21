package com.takeout.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.delivery.entity.RiderLocation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 骑手位置服务接口
 */
public interface RiderLocationService extends IService<RiderLocation> {

    /**
     * 记录骑手位置
     *
     * @param location 位置信息
     */
    void recordLocation(RiderLocation location);

    /**
     * 获取骑手轨迹
     *
     * @param riderId 骑手ID
     * @param start   开始时间
     * @param end     结束时间
     * @return 位置记录列表
     */
    List<RiderLocation> getRider轨迹(Long riderId, LocalDateTime start, LocalDateTime end);
}
