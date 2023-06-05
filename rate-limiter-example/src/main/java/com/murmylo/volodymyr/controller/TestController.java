package com.murmylo.volodymyr.controller;

import com.murmylo.volodymyr.ratelimiter.core.limit.RateLimitRule;
import com.murmylo.volodymyr.ratelimiter.core.limit.RequestLimitRule;
import com.murmylo.volodymyr.ratelimiter.core.limiter.ReactiveRateLimiter;
import com.murmylo.volodymyr.ratelimiter.core.limiter.RedisRateLimiterFactory;
import io.lettuce.core.RedisClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@RestController
public class TestController {
    private final RedisClient redisClient;
    private final RedisRateLimiterFactory factory;
    private final ReactiveRateLimiter limiter;

    public TestController(RedisClient redisClient) {
        this.redisClient = redisClient;
        factory = new RedisRateLimiterFactory(this.redisClient);
        limiter = factory.create();
    }

    @GetMapping("/api/hello3")
    public Mono<ResponseEntity<?>> helloworld() {
        Mono<Boolean> limit = limiter.overLimit("ip-1", Set.of(RequestLimitRule.of(Duration.of(120, ChronoUnit.SECONDS), 10).withPrecision(Duration.of(30, ChronoUnit.SECONDS))));
        return limit.flatMap(isLimitExceeded -> {
            if (isLimitExceeded) {
                return Mono.just(ResponseEntity.status(429).build());
            } else {
                return Mono.just(ResponseEntity.ok("hello world"));
            }
        });
    }

    @GetMapping("/api/hello")
    @RateLimitRule(duration = 60, limit = 10, precision = 2, name = "hello-endpoint")
    @RateLimitRule(duration = 60, limit = 10, precision = 2, name = "hello-endpoint")
    public Mono<String> hello() {
        return Mono.just("Hello world!");
    }
}
