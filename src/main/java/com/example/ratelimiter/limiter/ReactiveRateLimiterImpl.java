package com.example.ratelimiter.limiter;

import com.example.ratelimiter.limit.RequestLimitRule;
import com.example.ratelimiter.loader.RedisScriptLoader;
import com.example.ratelimiter.serializer.LimitRuleJsonSerializer;
import io.lettuce.core.api.reactive.RedisKeyReactiveCommands;
import io.lettuce.core.api.reactive.RedisScriptingReactiveCommands;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.Set;

import static io.lettuce.core.ScriptOutputType.VALUE;
import static java.util.Objects.requireNonNull;

@Slf4j
public class ReactiveRateLimiterImpl implements ReactiveRateLimiter {
    private final RedisScriptLoader scriptLoader;
    private final RedisScriptingReactiveCommands<String, String> redisScriptingCommands;
    private final RedisKeyReactiveCommands<String, String> redisKeyCommands;

    public ReactiveRateLimiterImpl(RedisScriptingReactiveCommands<String, String> redisScriptingReactiveCommands, RedisKeyReactiveCommands<String, String> redisKeyCommands) {
        requireNonNull(redisScriptingReactiveCommands, "redisScriptingReactiveCommands can not be null");
        requireNonNull(redisKeyCommands, "redisKeyCommands can not be null");
        this.redisScriptingCommands = redisScriptingReactiveCommands;
        this.redisKeyCommands = redisKeyCommands;
        scriptLoader = RedisScriptLoader.getInstance(redisScriptingReactiveCommands, "sliding-window-ratelimit.lua");
    }

    @Override
    public Mono<Boolean> overLimit(String key, Set<RequestLimitRule> requestLimitRules) {
        requireNonNull(key);
        String rulesJson = LimitRuleJsonSerializer.encode(requestLimitRules);
        return redisScriptingCommands
                .evalsha(scriptLoader.getScriptSha(), VALUE, new String[]{key}, rulesJson, String.valueOf(System.currentTimeMillis() / 1000))
                .retryWhen(Retry.max(1))
                .single()
                .map("1"::equals)
                .doOnSuccess(over -> {
                    if (over) {
                        log.debug("Requests matched by key '{}' are greater than the limit", key);
                    }
                });
    }

    @Override
    public Mono<Boolean> resetLimit(String key) {
        return redisKeyCommands.del(key).map(count -> count > 0);
    }
}
