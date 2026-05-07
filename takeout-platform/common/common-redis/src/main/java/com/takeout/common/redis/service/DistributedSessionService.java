package com.takeout.common.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Distributed session service backed by Redis.
 * Stores user session data (login state, user info) in Redis with configurable TTL.
 */
@Service
@RequiredArgsConstructor
public class DistributedSessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "takeout:session:";
    private static final long DEFAULT_TTL_HOURS = 24;

    /**
     * Create or update a user session.
     */
    public void createSession(String token, Map<String, Object> sessionData) {
        String key = SESSION_PREFIX + token;
        redisTemplate.opsForHash().putAll(key, sessionData);
        redisTemplate.expire(key, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }

    /**
     * Get all session data for a token.
     */
    public Map<Object, Object> getSession(String token) {
        String key = SESSION_PREFIX + token;
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Get a specific session field.
     */
    @SuppressWarnings("unchecked")
    public <T> T getSessionField(String token, String field) {
        String key = SESSION_PREFIX + token;
        return (T) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * Update a specific session field.
     */
    public void setSessionField(String token, String field, Object value) {
        String key = SESSION_PREFIX + token;
        redisTemplate.opsForHash().put(key, field, value);
        redisTemplate.expire(key, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }

    /**
     * Destroy a session (logout).
     */
    public Boolean destroySession(String token) {
        String key = SESSION_PREFIX + token;
        return redisTemplate.delete(key);
    }

    /**
     * Check if a session exists and is valid.
     */
    public boolean sessionExists(String token) {
        String key = SESSION_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Refresh session TTL on activity.
     */
    public void refreshSession(String token) {
        String key = SESSION_PREFIX + token;
        redisTemplate.expire(key, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }
}
