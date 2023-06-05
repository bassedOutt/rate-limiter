package com.murmylo.volodymyr.ratelimiter.core.limiter;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

import static java.util.Objects.requireNonNull;

public class RedisRateLimiterFactory implements RequestRateLimiterFactory {
    private final RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    public RedisRateLimiterFactory(RedisClient client) {
        this.client = requireNonNull(client);
    }

    @Override
    public ReactiveRateLimiter create() {
        getConnection().reactive();
        return new ReactiveRateLimiterImpl(getConnection().reactive(), getConnection().reactive());
    }

    @Override
    public void close() {
        client.shutdown();
    }

    private StatefulRedisConnection<String, String> getConnection() {
        // going to ignore race conditions at the cost of having multiple connections
        if (connection == null) {
            connection = client.connect();
        }
        return connection;
    }
}