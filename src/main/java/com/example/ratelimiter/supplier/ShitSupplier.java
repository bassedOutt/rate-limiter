package com.example.ratelimiter.supplier;

import com.example.ratelimiter.limit.RequestLimitRule;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class ShitSupplier implements RuleSupplier {
    @Override
    public Set<RequestLimitRule> get(String key) {
        return Set.of(RequestLimitRule.of(Duration.of(100, ChronoUnit.SECONDS), 10));
    }
}
