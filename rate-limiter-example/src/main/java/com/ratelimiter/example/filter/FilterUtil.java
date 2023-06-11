package com.ratelimiter.example.filter;

import com.ratelimiter.core.limit.RequestLimitRule;
import com.ratelimiter.core.limiter.ReactiveRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;

import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilterUtil {
    private final ReactiveRateLimiter rateLimiter;

    public Mono<Void> handleLimitCheck(ServerWebExchange exchange, WebFilterChain chain, String key, Set<RequestLimitRule> limitRules) {
        ServerHttpResponse response = exchange.getResponse();
        return rateLimiter.overLimit(key, limitRules).flatMap(isOverLimit -> {
            if (Boolean.TRUE.equals(isOverLimit)) {
                log.info("Request forbidden as limit was exceeded. Limit: {}", limitRules);
                response.setStatusCode(HttpStatusCode.valueOf(429));
                return response.setComplete();
            }
            log.info("Request not forbidden");
            return chain.filter(exchange);
        });
    }

    public String buildRequestLimitKey(ServerHttpRequest request, HandlerMethod method) {
        String ip = requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        String methodName = method.getMethod().getName();
        return ip + "-" + methodName;
    }
}
