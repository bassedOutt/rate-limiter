package com.ratelimiter.core.limiter;

import com.ratelimiter.core.supplier.TimeSupplier;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

import static java.util.Objects.requireNonNull;

public class RedisRateLimiterFactory implements RequestRateLimiterFactory {
    private final RedisClient client;
    private final TimeSupplier timeSupplier;
    private StatefulRedisConnection<String, String> connection;

    public RedisRateLimiterFactory(RedisClient client, TimeSupplier timeSupplier) {
        this.client = requireNonNull(client);
        this.timeSupplier = requireNonNull(timeSupplier);
    }

    @Override
    public ReactiveRateLimiter create() {
        var reactiveConnection = getConnection().reactive();
        return new ReactiveRateLimiterImpl(reactiveConnection, reactiveConnection, timeSupplier);
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