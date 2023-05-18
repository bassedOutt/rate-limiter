package com.example.ratelimiter.limiter;


import com.example.ratelimiter.supplier.RuleSupplier;

import java.io.Closeable;

public interface RequestRateLimiterFactory extends Closeable {
    ReactiveRateLimiter create(RuleSupplier ruleSupplier);

    void close();
}
