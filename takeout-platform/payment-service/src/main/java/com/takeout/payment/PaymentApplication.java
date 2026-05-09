package com.takeout.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 支付服务启动类
 * 职责：统一下单、回调处理、退款、支付流水管理
 */
@SpringBootApplication(scanBasePackages = {"com.takeout.payment", "com.takeout.common"})
@EnableDiscoveryClient
@MapperScan("com.takeout.payment.mapper")
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
