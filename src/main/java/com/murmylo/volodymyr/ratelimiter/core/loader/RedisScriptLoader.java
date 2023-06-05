package com.murmylo.volodymyr.ratelimiter.core.loader;


import com.murmylo.volodymyr.ratelimiter.core.exception.ScriptLoadException;
import io.lettuce.core.api.reactive.RedisScriptingReactiveCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class RedisScriptLoader {
    private static final Logger LOG = LoggerFactory.getLogger(RedisScriptLoader.class);
    private final RedisScriptingReactiveCommands<String, String> redisScriptingCommands;
    private final String scriptUri;
    private static RedisScriptLoader redisScriptLoader;
    private final String scriptSha;

    private RedisScriptLoader(RedisScriptingReactiveCommands<String, String> redisScriptingCommands, String scriptUri) {
        this.redisScriptingCommands = requireNonNull(redisScriptingCommands);
        this.scriptUri = requireNonNull(scriptUri);
        try {
            scriptSha = loadScript().block(Duration.of(10, ChronoUnit.SECONDS));
        } catch (RuntimeException exception) {
            throw new ScriptLoadException("Failed to load lua script", exception.getCause());
        }
    }

    private Mono<String> loadScript() {
        String script;
        try {
            script = readScriptFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load script file");
        }

        return redisScriptingCommands.scriptLoad(script);
    }

    private String readScriptFile() throws IOException {
        URL url = RedisScriptLoader.class.getClassLoader().getResource(scriptUri);
        if (url == null) {
            throw new IllegalArgumentException("script '" + scriptUri + "' not found");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private synchronized String resetScript() {
        return redisScriptingCommands.scriptFlush().block(Duration.of(5, ChronoUnit.SECONDS));
    }

    public String getScriptSha() {
        return scriptSha;
    }

    public static synchronized RedisScriptLoader getInstance(RedisScriptingReactiveCommands<String, String> redisScriptingCommands, String scriptUri) {
        if (redisScriptLoader != null) {
            return redisScriptLoader;
        } else {
            redisScriptLoader = new RedisScriptLoader(redisScriptingCommands, scriptUri);
        }
        return redisScriptLoader;
    }
}
