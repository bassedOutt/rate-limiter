package com.example.ratelimiter.limiter;



import java.io.Closeable;

public interface RequestRateLimiterFactory extends Closeable {
    ReactiveRateLimiter create();

    void close();
}
