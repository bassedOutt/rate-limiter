package com.example.ratelimiter.handler;

import com.example.ratelimiter.limit.RateLimitRule;
import com.example.ratelimiter.limit.RateLimited;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class Handler {
    @RateLimited({
            @RateLimitRule(duration = 60, limit = 10, precision = 2, name = "hello-endpoint")
    })
    public Mono<ServerResponse> hello() {
        log.info("Attempt to process request");
        return ServerResponse.ok().body("hello world", String.class);
    }
}
