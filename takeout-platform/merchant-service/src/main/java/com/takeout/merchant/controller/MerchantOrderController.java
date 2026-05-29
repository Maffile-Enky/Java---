package com.takeout.merchant.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.merchant.service.MerchantOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant/order")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final MerchantOrderService merchantOrderService;

    /**
     * 商家接单
     */
    @PostMapping("/accept")
    public Result<Void> acceptOrder(@RequestParam String orderId, @RequestParam Long merchantId) {
        merchantOrderService.acceptOrder(orderId, merchantId);
        return Result.success(null);
    }

    /**
     * 商家出餐
     */
    @PostMapping("/ready")
    public Result<Void> readyOrder(@RequestParam String orderId, @RequestParam Long merchantId) {
        merchantOrderService.readyOrder(orderId, merchantId);
        return Result.success(null);
    }
}