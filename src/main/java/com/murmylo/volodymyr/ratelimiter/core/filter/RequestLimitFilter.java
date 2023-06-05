package com.murmylo.volodymyr.ratelimiter.core.filter;

import com.murmylo.volodymyr.ratelimiter.core.limit.RateLimitRule;
import com.murmylo.volodymyr.ratelimiter.core.limit.RequestLimitRule;
import com.murmylo.volodymyr.ratelimiter.core.limiter.ReactiveRateLimiter;
import com.murmylo.volodymyr.ratelimiter.core.mapper.RequestLimitRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Component
public class RequestLimitFilter implements WebFilter {
    private final ReactiveRateLimiter rateLimiter;
    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Mono<HandlerMethod> handlerMethod = this.handlerMapping.getHandler(exchange).map(el -> (HandlerMethod) el);
        return handlerMethod.flatMap(
                method -> {
                    ServerHttpRequest request = exchange.getRequest();
                    RateLimitRule[] rules = method.getMethod().getAnnotationsByType(RateLimitRule.class);
                    String key = buildRequestLimitKey(request, method);
                    Set<RequestLimitRule> limitRules = getRequestLimitRules(rules);
                    return handleLimitCheck(exchange, chain, key, limitRules);
                });
    }

    private static String buildRequestLimitKey(ServerHttpRequest request, HandlerMethod method) {
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        String methodName = method.getMethod().getName();
        return ip + "-" + methodName;
    }

    private static Set<RequestLimitRule> getRequestLimitRules(RateLimitRule[] rules) {
        Set<RequestLimitRule> limitRules = new HashSet<>();
        for (RateLimitRule rule : rules) {
            RequestLimitRule requestRule = RequestLimitRuleMapper.mapFromAnnotation(rule);
            limitRules.add(requestRule);
        }
        return limitRules;
    }

    private Mono<Void> handleLimitCheck(ServerWebExchange exchange, WebFilterChain chain, String key, Set<RequestLimitRule> limitRules) {
        ServerHttpResponse response = exchange.getResponse();
        return rateLimiter.overLimit(key, limitRules).flatMap(isOverLimit -> {
            if (isOverLimit) {
                log.info("Request forbidden as limit was exceeded. Limit: {}", limitRules);
                response.setStatusCode(HttpStatusCode.valueOf(429));
                return response.setComplete();
            }
            log.info("Request not forbidden");
            return chain.filter(exchange);
        });
    }
}