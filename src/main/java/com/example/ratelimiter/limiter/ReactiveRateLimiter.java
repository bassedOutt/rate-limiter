package com.example.ratelimiter.limiter;

import com.example.ratelimiter.limit.RequestLimitRule;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ReactiveRateLimiter {
    Mono<Boolean> overLimit(String key, Set<RequestLimitRule> requestLimitRules);

    Mono<Boolean> resetLimit(String key);
}
