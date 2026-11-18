package com.kodat.complexhotelsystem.services;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RateLimitService {

    private static final DefaultRedisScript<String> INCREMENT_SCRIPT =
            new DefaultRedisScript<>("""
                    local count = redis.call('INCR', KEYS[1])
                    if count == 1 then
                        redis.call('PEXPIRE', KEYS[1], ARGV[1])
                    end
                    local ttl = redis.call('PTTL', KEYS[1])
                    return tostring(count) .. ':' .. tostring(ttl)
                    """, String.class);

    private final StringRedisTemplate redisTemplate;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Decision checkLimit(String key, int maxRequests, long windowMillis) {
        String result = redisTemplate.execute(
                INCREMENT_SCRIPT,
                List.of(key),
                Long.toString(windowMillis)
        );

        if (result == null) {
            throw new IllegalStateException("Redis returned no rate-limit result");
        }

        String[] values = result.split(":", 2);
        long count = Long.parseLong(values[0]);
        long ttlMillis = Long.parseLong(values[1]);

        if (count <= maxRequests) {
            return new Decision(true, 0);
        }

        long retryAfterSeconds = Math.max(1, (ttlMillis + 999) / 1000);
        return new Decision(false, retryAfterSeconds);
    }

    public record Decision(boolean allowed, long retryAfterSeconds) {}
}
