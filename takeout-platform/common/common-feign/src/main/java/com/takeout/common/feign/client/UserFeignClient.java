package com.takeout.common.feign.client;

import com.takeout.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    Result<?> getUserById(@PathVariable("id") Long id);

    @GetMapping("/user/address/{id}")
    Result<?> getAddressById(@PathVariable("id") Long id);
}
