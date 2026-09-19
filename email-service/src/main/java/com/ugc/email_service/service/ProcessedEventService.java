package com.ugc.email_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProcessedEventService {

    private final StringRedisTemplate redisTemplate;

    public boolean isAlreadyProcessed(Long ticketId){
        String key = "email:ticket:" + ticketId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void markAsProcessed(Long ticketId){
        String key = "email:ticket:" + ticketId;
        redisTemplate.opsForValue().set(
                key,
                "Processed",
                Duration.ofDays(7)
        );
    }
}
