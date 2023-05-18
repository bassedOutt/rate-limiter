package com.example.ratelimiter.limiter;

import com.example.ratelimiter.supplier.RuleSupplier;
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
    public ReactiveRateLimiter create(RuleSupplier ruleSupplier) {
        getConnection().reactive();
        return new ReactiveRateLimiterImpl(getConnection().reactive(), getConnection().reactive(), ruleSupplier);
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