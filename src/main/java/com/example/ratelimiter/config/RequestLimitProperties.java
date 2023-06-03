package com.example.ratelimiter.config;

import com.example.ratelimiter.limit.RequestLimitRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "request-limits")
@Getter
@Setter
class RequestLimitProperties {
    private Map<String, Set<RequestLimitRule>> limits;

}