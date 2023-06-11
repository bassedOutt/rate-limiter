package com.ratelimiter.example.filter;

import com.ratelimiter.core.limit.RateLimitRule;
import com.ratelimiter.core.limit.RequestLimitRule;
import com.ratelimiter.core.mapper.RequestLimitRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
//@Component
public class AnnotationBasedRequestLimitFilter implements WebFilter {
    private final RequestMappingHandlerMapping handlerMapping;
    private final FilterUtil filterUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Mono<HandlerMethod> handlerMethod = this.handlerMapping.getHandler(exchange).map(el -> (HandlerMethod) el);
        return handlerMethod.flatMap(
                method -> {
                    ServerHttpRequest request = exchange.getRequest();
                    RateLimitRule[] rules = method.getMethod().getAnnotationsByType(RateLimitRule.class);
                    if (rules.length == 0) {
                        return chain.filter(exchange);
                    }
                    String key = filterUtil.buildRequestLimitKey(request, method);
                    Set<RequestLimitRule> limitRules = getRequestLimitRules(rules);
                    return filterUtil.handleLimitCheck(exchange, chain, key, limitRules);
                });
    }

    private static Set<RequestLimitRule> getRequestLimitRules(RateLimitRule[] rules) {
        Set<RequestLimitRule> limitRules = new HashSet<>();
        for (RateLimitRule rule : rules) {
            RequestLimitRule requestRule = RequestLimitRuleMapper.mapFromAnnotation(rule);
            limitRules.add(requestRule);
        }
        return limitRules;
    }
}