package com.example.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    public void store(String email, String token) {
        redisTemplate.opsForValue().set(email, token, Duration.ofDays(7));
    }

    public String getTokenByEmail(String email) {
        return redisTemplate.opsForValue().get(email);
    }
}
