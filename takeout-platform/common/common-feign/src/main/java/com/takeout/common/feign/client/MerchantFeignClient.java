package com.takeout.common.feign.client;

import com.takeout.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "merchant-service")
public interface MerchantFeignClient {

    @GetMapping("/merchant/{id}")
    Result<?> getMerchantById(@PathVariable("id") Long id);

    @GetMapping("/dish/{id}")
    Result<?> getDishById(@PathVariable("id") Long id);
}
