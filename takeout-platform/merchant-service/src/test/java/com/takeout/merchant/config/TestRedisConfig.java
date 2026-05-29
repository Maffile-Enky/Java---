package com.takeout.merchant.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Configuration
public class TestRedisConfig {

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        try {
            redisServer = new RedisServer(6379);
            redisServer.start();
            log.info("Embedded Redis server started on port 6379");
        } catch (Exception e) {
            log.warn("Failed to start embedded Redis: {}", e.getMessage());
            log.warn("Assuming external Redis is running");
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
            log.info("Embedded Redis server stopped");
        }
    }
}
