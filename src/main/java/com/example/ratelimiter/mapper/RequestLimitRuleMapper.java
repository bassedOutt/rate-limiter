package com.example.ratelimiter.mapper;

import com.example.ratelimiter.limit.RateLimitRule;
import com.example.ratelimiter.limit.RequestLimitRule;

public class RequestLimitRuleMapper {
    public static RequestLimitRule mapFromAnnotation(RateLimitRule annotation) {
        int duration = annotation.duration();
        long limit = annotation.limit();
        int precision = annotation.precision();
        String name = annotation.name();

        return new RequestLimitRule(duration, limit, precision, name);
    }
}
