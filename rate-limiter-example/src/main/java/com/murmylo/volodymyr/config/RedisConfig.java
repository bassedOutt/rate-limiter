package com.murmylo.volodymyr.config;

import com.murmylo.volodymyr.ratelimiter.core.limiter.ReactiveRateLimiter;
import com.murmylo.volodymyr.ratelimiter.core.limiter.RedisRateLimiterFactory;
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
