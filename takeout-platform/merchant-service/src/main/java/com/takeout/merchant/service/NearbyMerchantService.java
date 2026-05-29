package com.takeout.merchant.service;

import com.takeout.merchant.dto.NearbyMerchantDTO;

import java.util.List;

public interface NearbyMerchantService {

    /**
     * 查询附近商家
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径（公里）
     * @return 附近商家列表
     */
    List<NearbyMerchantDTO> findNearbyMerchants(Double longitude, Double latitude, Double radius);
}