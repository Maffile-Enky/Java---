package com.takeout.common.feign.client;

import com.takeout.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient {

    @GetMapping("/payment/status/{paymentNo}")
    Result<?> getPaymentByOrderNo(@PathVariable("paymentNo") String orderNo);
}
