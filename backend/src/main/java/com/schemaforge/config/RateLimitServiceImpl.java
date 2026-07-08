package com.schemaforge.config;

import com.schemaforge.common.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private static final String PREFIX = "rl:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void checkAndIncrement(String key, int maxRequests, int windowSeconds, String operation) {
        String redisKey = PREFIX + key;
        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1) {
                // First request — set expiry
                redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
            }
            if (count != null && count > maxRequests) {
                log.warn("Rate limit exceeded: key={} count={} max={}", redisKey, count, maxRequests);
                throw RateLimitExceededException.forOperation(operation, maxRequests, windowSeconds);
            }
        } catch (RateLimitExceededException ex) {
            throw ex;
        } catch (Exception ex) {
            // Redis unavailable — fail open (do not block the request)
            log.error("Rate limit check failed for key={}: {} — failing open", redisKey, ex.getMessage());
        }
    }

    @Override
    public long remainingRequests(String key, int maxRequests, int windowSeconds) {
        try {
            String value = redisTemplate.opsForValue().get(PREFIX + key);
            if (value == null) return maxRequests;
            long used = Long.parseLong(value);
            return Math.max(0, maxRequests - used);
        } catch (Exception ex) {
            return -1;
        }
    }
}