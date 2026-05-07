package com.takeout.common.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * SMS verification code service with Redis storage and rate limiting.
 */
@Service
@RequiredArgsConstructor
public class SmsCodeService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String CODE_PREFIX = "takeout:sms:code:";
    private static final String RATE_PREFIX = "takeout:sms:rate:";
    private static final long CODE_TTL_MINUTES = 5;
    private static final long RATE_LIMIT_SECONDS = 60;

    /**
     * Save SMS code for a phone number.
     */
    public void saveCode(String phone, String code) {
        stringRedisTemplate.opsForValue().set(CODE_PREFIX + phone, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        // Rate limit: prevent sending again within 60s
        stringRedisTemplate.opsForValue().set(RATE_PREFIX + phone, "1", RATE_LIMIT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Verify SMS code. Returns true if valid, then deletes the code.
     */
    public boolean verifyCode(String phone, String code) {
        String key = CODE_PREFIX + phone;
        String stored = stringRedisTemplate.opsForValue().get(key);
        if (code.equals(stored)) {
            stringRedisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /**
     * Check if the phone number is rate-limited (sent SMS too recently).
     */
    public boolean isRateLimited(String phone) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(RATE_PREFIX + phone));
    }
}
