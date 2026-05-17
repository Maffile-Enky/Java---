package com.takeout.common.feign.client;

import com.takeout.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "search-service", path = "/search")
public interface SearchFeignClient {

    @PostMapping("/sync/merchant")
    Result<Void> syncMerchant(@RequestBody Map<String, Object> merchant);

    @PostMapping("/sync/dish")
    Result<Void> syncDish(@RequestBody Map<String, Object> dish);
}
