package com.ratelimiter.example.filter;

import com.ratelimiter.core.limit.RequestLimitRule;
import com.ratelimiter.example.config.RequestLimitProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This filter is used for rate limiting.
 * Configuration is based on yaml config.
 * Key in yaml should be contoller method name
 */
@Slf4j
@Component
public class YamlBasedRequestLimitFilter implements WebFilter {
    private final FilterUtil filterUtil;
    private final RequestMappingHandlerMapping handlerMapping;
    private final Map<String, Set<RequestLimitRule>> requestLimitRuleMap;

    public YamlBasedRequestLimitFilter(FilterUtil filterUtil, RequestMappingHandlerMapping handlerMapping, RequestLimitProperties requestLimitProperties) {
        this.filterUtil = filterUtil;
        this.handlerMapping = handlerMapping;
        this.requestLimitRuleMap = requestLimitProperties.getLimits();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Mono<HandlerMethod> handlerMethod = this.handlerMapping.getHandler(exchange).map(el -> (HandlerMethod) el);
        return handlerMethod.flatMap(
                method -> {
                    ServerHttpRequest request = exchange.getRequest();
                    Set<RequestLimitRule> rules = requestLimitRuleMap.getOrDefault(method.getMethod().getName(), Collections.emptySet());
                    if (rules.isEmpty()) {
                        return chain.filter(exchange);
                    }
                    String key = filterUtil.buildRequestLimitKey(request, method);
                    return filterUtil.handleLimitCheck(exchange, chain, key, rules);
                });
    }

}
