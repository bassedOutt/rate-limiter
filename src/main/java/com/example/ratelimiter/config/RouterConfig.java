package com.example.ratelimiter.config;

import com.example.ratelimiter.handler.Handler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> route(Handler handler) {
        return RouterFunctions
                .route()
                .GET("/hello", (request -> handler.hello()))
                .build();
    }
}
