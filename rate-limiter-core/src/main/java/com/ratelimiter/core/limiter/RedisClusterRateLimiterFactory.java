package com.ratelimiter.core.limiter;

import com.ratelimiter.core.supplier.TimeSupplier;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;

import static java.util.Objects.requireNonNull;

public class RedisClusterRateLimiterFactory implements RequestRateLimiterFactory {
    private final RedisClusterClient client;
    private final TimeSupplier timeSupplier;
    private StatefulRedisClusterConnection<String, String> connection;

    public RedisClusterRateLimiterFactory(RedisClusterClient client, TimeSupplier timeSupplier) {
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

    private StatefulRedisClusterConnection<String, String> getConnection() {
        // going to ignore race conditions at the cost of having multiple connections
        if (connection == null) {
            connection = client.connect();
        }
        return connection;
    }
}