package com.ugc.api_gateway.service;

import com.ugc.api_gateway.config.TooManyRequestsException;
import io.github.resilience4j.ratelimiter.RateLimiter;
import org.springframework.stereotype.Service;

@Service
public class GatewayRateLimitService {

    private final RateLimiter rateLimiter;

    public GatewayRateLimitService(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public void checkRateLimit() {

        if (!rateLimiter.acquirePermission()) {
            throw new TooManyRequestsException(
                    "Too many requests. Please try again later."
            );
        }
    }
}