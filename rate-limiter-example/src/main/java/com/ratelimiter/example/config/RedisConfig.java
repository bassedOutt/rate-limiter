package com.ratelimiter.example.config;

import com.ratelimiter.core.limiter.ReactiveRateLimiter;
import com.ratelimiter.core.limiter.RedisRateLimiterFactory;
import com.ratelimiter.core.supplier.TimeSupplier;
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
    public TimeSupplier timeSupplier() {
        return () -> System.currentTimeMillis() / 1000;
    }

    @Bean
    public ReactiveRateLimiter rateLimiter() {
        RedisRateLimiterFactory factory = new RedisRateLimiterFactory(redisClient(), timeSupplier());
        return factory.create();
    }
}
