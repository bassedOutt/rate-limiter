package com.example.ratelimiter.controller;

import com.example.ratelimiter.limiter.ReactiveRateLimiter;
import com.example.ratelimiter.limiter.RedisRateLimiterFactory;
import com.example.ratelimiter.supplier.ShitSupplier;
import io.lettuce.core.RedisClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RestController
public class TestController {

    private final RedisClient redisClient;

    private final RedisRateLimiterFactory factory;

    private final ReactiveRateLimiter limiter;

    public TestController(RedisClient redisClient) {
        this.redisClient = redisClient;
        factory = new RedisRateLimiterFactory(this.redisClient);
        limiter = factory.create(new ShitSupplier());
    }

    @GetMapping("/api/hello")
    public ResponseEntity<String> hello() {
        if (!limiter.overLimit("remote-ip:" + "1").block(Duration.of(3, ChronoUnit.SECONDS))) {
            return ResponseEntity.status(429).build();
        }
        return ResponseEntity.ok().body("helloworld");
    }

}
