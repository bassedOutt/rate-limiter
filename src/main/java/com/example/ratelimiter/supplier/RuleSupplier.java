package com.example.ratelimiter.supplier;

import com.example.ratelimiter.limit.RequestLimitRule;

import java.util.Set;

public interface RuleSupplier {
    Set<RequestLimitRule> get(String key);
}
