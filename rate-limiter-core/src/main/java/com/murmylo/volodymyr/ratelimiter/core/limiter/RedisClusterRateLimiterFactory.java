package com.murmylo.volodymyr.ratelimiter.core.limiter;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;

import static java.util.Objects.requireNonNull;

public class RedisClusterRateLimiterFactory implements RequestRateLimiterFactory {
    private final RedisClusterClient client;
    private StatefulRedisClusterConnection<String, String> connection;

    public RedisClusterRateLimiterFactory(RedisClusterClient client) {
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

    private StatefulRedisClusterConnection<String, String> getConnection() {
        // going to ignore race conditions at the cost of having multiple connections
        if (connection == null) {
            connection = client.connect();
        }
        return connection;
    }
}