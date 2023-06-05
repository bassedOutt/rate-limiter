package com.ratelimiter.core.limit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RateLimitRules.class)
public @interface RateLimitRule {
    int duration();
    long limit();
    int precision();
    String name();

}
