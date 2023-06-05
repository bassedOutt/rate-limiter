package com.ratelimiter.core.limit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimitRules {
    RateLimitRule[] value();
}