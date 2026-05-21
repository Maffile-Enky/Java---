package com.takeout.delivery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 配送服务启动类
 * 职责：骑手管理、配送任务调度、实时位置追踪
 */
@SpringBootApplication(scanBasePackages = {"com.takeout.delivery", "com.takeout.common"})
@EnableDiscoveryClient
@MapperScan("com.takeout.delivery.mapper")
public class DeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
