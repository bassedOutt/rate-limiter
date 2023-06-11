package com.ratelimiter.example.config;

import com.ratelimiter.core.limit.RequestLimitRule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Getter
@ConfigurationProperties(prefix = "request-limits")
@RequiredArgsConstructor
@Setter
public class RequestLimitProperties {
    private Map<String, Set<RequestLimitRule>> limits;
}