package com.ratelimiter.core.mapper;

import com.ratelimiter.core.limit.RateLimitRule;
import com.ratelimiter.core.limit.RequestLimitRule;

public class RequestLimitRuleMapper {
    public static RequestLimitRule mapFromAnnotation(RateLimitRule annotation) {
        int duration = annotation.duration();
        long limit = annotation.limit();
        int precision = annotation.precision();
        String name = annotation.name();

        return new RequestLimitRule(duration, limit, precision, name);
    }
}
