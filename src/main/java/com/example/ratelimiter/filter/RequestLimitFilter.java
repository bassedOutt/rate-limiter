package com.example.ratelimiter.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class RequestLimitFilter implements WebFilter {

//    private final Map<String, Set<RequestLimitRule>> requestLimitsMap;

    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Mono<HandlerMethod> handlerMethod = this.handlerMapping.getHandler(exchange).map(el -> (HandlerMethod) el);
        return handlerMethod.flatMap(
                method -> {
                    log.info("Handler method" + method.getMethod().getName());
                    return chain.filter(exchange);
                }
        );
    }

}
