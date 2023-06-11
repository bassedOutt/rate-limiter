package com.ratelimiter.core.limiter;

import com.ratelimiter.core.limit.RequestLimitRule;
import com.ratelimiter.core.loader.RedisScriptLoader;
import com.ratelimiter.core.serializer.LimitRuleJsonSerializer;
import com.ratelimiter.core.supplier.TimeSupplier;
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
    public static final String SLIDING_WINDOW_LUA_SCRIPT_PATH = "sliding-window-ratelimit.lua";
    private final RedisScriptLoader scriptLoader;
    private final RedisScriptingReactiveCommands<String, String> redisScriptingCommands;
    private final RedisKeyReactiveCommands<String, String> redisKeyCommands;
    private final TimeSupplier timeSupplier;

    ReactiveRateLimiterImpl(RedisScriptingReactiveCommands<String, String> redisScriptingReactiveCommands, RedisKeyReactiveCommands<String, String> redisKeyCommands, TimeSupplier timeSupplier) {
        requireNonNull(redisScriptingReactiveCommands, "redisScriptingReactiveCommands can not be null");
        requireNonNull(redisKeyCommands, "redisKeyCommands can not be null");
        requireNonNull(timeSupplier, "timeSupplier can not be null");
        this.redisScriptingCommands = redisScriptingReactiveCommands;
        this.redisKeyCommands = redisKeyCommands;
        this.timeSupplier = timeSupplier;
        scriptLoader = RedisScriptLoader.getInstance(redisScriptingReactiveCommands, SLIDING_WINDOW_LUA_SCRIPT_PATH);
    }

    @Override
    public Mono<Boolean> overLimit(String key, Set<RequestLimitRule> requestLimitRules) {
        requireNonNull(key);
        String rulesJson = LimitRuleJsonSerializer.encode(requestLimitRules);
        return redisScriptingCommands
                .evalsha(scriptLoader.getScriptSha(), VALUE, new String[]{key}, rulesJson, String.valueOf(timeSupplier.getCurrentTime()))
                .retryWhen(Retry.max(1))
                .single()
                .map("1"::equals)
                .doOnSuccess(over -> {
                    if (Boolean.TRUE.equals(over)) {
                        log.debug("Requests matched by key '{}' are greater than the limit", key);
                    }
                });
    }

    @Override
    public Mono<Boolean> resetLimit(String key) {
        return redisKeyCommands.del(key).map(count -> count > 0);
    }
}
