package com.takeout.merchant.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.merchant.dto.NearbyMerchantDTO;
import com.takeout.merchant.service.NearbyMerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant/nearby")
@RequiredArgsConstructor
@Tag(name = "附近商家", description = "基于地理位置的商家查询接口")
public class NearbyMerchantController {

    private final NearbyMerchantService nearbyMerchantService;

    @GetMapping
    @Operation(summary = "查询附近商家", description = "根据用户当前位置查询附近的商家")
    public Result<List<NearbyMerchantDTO>> findNearby(
            @Parameter(description = "用户当前经度") @RequestParam Double longitude,
            @Parameter(description = "用户当前纬度") @RequestParam Double latitude,
            @Parameter(description = "搜索半径（公里），默认5公里") @RequestParam(defaultValue = "5") Double radius) {
        List<NearbyMerchantDTO> merchants = nearbyMerchantService.findNearbyMerchants(longitude, latitude, radius);
        return Result.success(merchants);
    }
}