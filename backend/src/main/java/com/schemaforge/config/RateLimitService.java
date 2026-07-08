package com.schemaforge.config;

/**
 * Redis-backed rate limiting service.
 * Keys are namespaced by operation and identity (userId or IP address).
 */
public interface RateLimitService {

    /**
     * Checks and increments the rate limit counter.
     * Throws {@link com.schemaforge.common.exception.RateLimitExceededException} if exceeded.
     *
     * @param key           unique identifier (e.g. "login:192.168.1.1" or "ai-gen:user-uuid")
     * @param maxRequests   maximum allowed requests in the window
     * @param windowSeconds sliding window duration in seconds
     * @param operation     human-readable operation name for error messages
     */
    void checkAndIncrement(String key, int maxRequests, int windowSeconds, String operation);

    /**
     * Returns remaining requests allowed in the current window. -1 if unlimited.
     */
    long remainingRequests(String key, int maxRequests, int windowSeconds);
}