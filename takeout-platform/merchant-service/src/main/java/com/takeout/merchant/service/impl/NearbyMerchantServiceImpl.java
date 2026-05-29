package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.merchant.dto.NearbyMerchantDTO;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.MerchantService;
import com.takeout.merchant.service.NearbyMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NearbyMerchantServiceImpl implements NearbyMerchantService {

    private final MerchantService merchantService;

    private static final double EARTH_RADIUS = 6371; // 地球半径（公里）

    @Override
    public List<NearbyMerchantDTO> findNearbyMerchants(Double longitude, Double latitude, Double radius) {
        // 查询所有营业中的商家
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getStatus, 1);
        List<Merchant> merchants = merchantService.list(wrapper);

        List<NearbyMerchantDTO> result = new ArrayList<>();

        for (Merchant merchant : merchants) {
            if (merchant.getLongitude() != null && merchant.getLatitude() != null) {
                double distance = calculateDistance(
                    longitude, latitude,
                    merchant.getLongitude().doubleValue(), merchant.getLatitude().doubleValue()
                );

                if (distance <= radius) {
                    NearbyMerchantDTO dto = new NearbyMerchantDTO();
                    dto.setId(merchant.getId());
                    dto.setName(merchant.getName());
                    dto.setAddress(merchant.getAddress());
                    dto.setLongitude(merchant.getLongitude().doubleValue());
                    dto.setLatitude(merchant.getLatitude().doubleValue());
                    dto.setDistance(distance);
                    result.add(dto);
                }
            }
        }

        // 按距离排序
        result.sort(Comparator.comparing(NearbyMerchantDTO::getDistance));

        return result;
    }

    /**
     * 使用 Haversine 公式计算两点之间的距离
     */
    private double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}