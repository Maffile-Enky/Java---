package com.takeout.common.feign.client;

import com.takeout.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderFeignClient {

    @GetMapping("/order/{id}")
    Result<?> getOrderDetail(@PathVariable("id") Long orderId);

    @GetMapping("/order/list")
    Result<?> listUserOrders(@RequestParam("userId") Long userId,
                             @RequestParam("page") int page,
                             @RequestParam("size") int size);
}
