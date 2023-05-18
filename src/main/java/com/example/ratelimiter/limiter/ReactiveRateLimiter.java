package com.example.ratelimiter.limiter;

import reactor.core.publisher.Mono;

public interface ReactiveRateLimiter {
    Mono<Boolean> overLimit(String key);

    Mono<Boolean> resetLimit(String key);
}
