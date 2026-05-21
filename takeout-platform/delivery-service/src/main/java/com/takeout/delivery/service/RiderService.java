package com.takeout.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.delivery.entity.Rider;

import java.math.BigDecimal;

/**
 * 骑手服务接口
 */
public interface RiderService extends IService<Rider> {

    /**
     * 注册骑手
     *
     * @param userId 关联用户ID
     * @param rider  骑手信息
     * @return 注册后的骑手信息
     */
    Rider registerRider(Long userId, Rider rider);

    /**
     * 根据用户ID获取骑手信息
     *
     * @param userId 用户ID
     * @return 骑手信息
     */
    Rider getRiderByUserId(Long userId);

    /**
     * 更新骑手状态
     *
     * @param riderId 骑手ID
     * @param status  目标状态（ONLINE/OFFLINE）
     * @return 更新后的骑手信息
     */
    Rider updateRiderStatus(Long riderId, String status);

    /**
     * 更新骑手位置
     *
     * @param riderId   骑手ID
     * @param longitude 经度
     * @param latitude  纬度
     */
    void updateRiderLocation(Long riderId, BigDecimal longitude, BigDecimal latitude);

    /**
     * 根据ID获取骑手信息
     *
     * @param riderId 骑手ID
     * @return 骑手信息
     */
    Rider getRiderById(Long riderId);
}
