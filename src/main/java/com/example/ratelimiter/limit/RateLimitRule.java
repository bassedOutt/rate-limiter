package com.example.ratelimiter.limit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimitRule {
    int duration();
    long limit();
    int precision();
    String name();

}
