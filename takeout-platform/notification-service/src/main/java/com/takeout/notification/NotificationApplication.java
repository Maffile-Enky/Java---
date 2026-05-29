package com.takeout.notification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 通知服务启动类
 * 职责：实时消息推送（WebSocket）、异步事件消费、通知记录管理
 */
@SpringBootApplication(scanBasePackages = {"com.takeout.notification", "com.takeout.common"})
@EnableDiscoveryClient
@MapperScan("com.takeout.notification.mapper")
@EnableScheduling
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
