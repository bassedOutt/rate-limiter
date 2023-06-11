package com.ratelimiter.core;

import com.ratelimiter.core.loader.RedisScriptLoader;
import io.lettuce.core.api.reactive.RedisScriptingReactiveCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RedisScriptLoaderTest {
    private RedisScriptingReactiveCommands<String, String> redisScriptingCommands;

    private static final Resource resource = new ClassPathResource("sliding-window-ratelimit.lua");

    @BeforeEach
    void init() {
        redisScriptingCommands = mock(RedisScriptingReactiveCommands.class);
    }

    @Test
    void testGetInstance() throws IOException {
        String scriptUri = "sliding-window-ratelimit.lua";
        String scriptSha = "script-sha";

        // Mock script URL and contents
        when(redisScriptingCommands.scriptLoad(anyString())).thenReturn(Mono.just(scriptSha));

        // Mock RedisScriptingReactiveCommands behavior
        when(redisScriptingCommands.scriptFlush()).thenReturn(Mono.empty());

        // Create RedisScriptLoader instance
        RedisScriptLoader redisScriptLoader = RedisScriptLoader.getInstance(redisScriptingCommands, scriptUri);

        // Verify script loading and sha retrieval
        assertEquals(scriptSha, redisScriptLoader.getScriptSha());

        // Verify RedisScriptingReactiveCommands interactions
        verify(redisScriptingCommands).scriptLoad(resource.getContentAsString(Charset.defaultCharset()));
    }
}
