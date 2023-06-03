package com.example.ratelimiter.config;

import com.example.ratelimiter.limiter.ReactiveRateLimiter;
import com.example.ratelimiter.limiter.RedisRateLimiterFactory;
import io.lettuce.core.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost");
    }
    @Bean
    public ReactiveRateLimiter rateLimiter() {
        RedisRateLimiterFactory factory = new RedisRateLimiterFactory(redisClient());
        return factory.create();
    }
}
