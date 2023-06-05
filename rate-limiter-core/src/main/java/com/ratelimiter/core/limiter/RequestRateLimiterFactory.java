package com.ratelimiter.core.limiter;



import java.io.Closeable;

public interface RequestRateLimiterFactory extends Closeable {
    ReactiveRateLimiter create();

    void close();
}
