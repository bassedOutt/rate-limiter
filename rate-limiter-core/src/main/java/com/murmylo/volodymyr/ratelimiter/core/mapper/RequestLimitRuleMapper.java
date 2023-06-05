package com.murmylo.volodymyr.ratelimiter.core.mapper;

import com.murmylo.volodymyr.ratelimiter.core.limit.RateLimitRule;
import com.murmylo.volodymyr.ratelimiter.core.limit.RequestLimitRule;

public class RequestLimitRuleMapper {
    public static RequestLimitRule mapFromAnnotation(RateLimitRule annotation) {
        int duration = annotation.duration();
        long limit = annotation.limit();
        int precision = annotation.precision();
        String name = annotation.name();

        return new RequestLimitRule(duration, limit, precision, name);
    }
}
