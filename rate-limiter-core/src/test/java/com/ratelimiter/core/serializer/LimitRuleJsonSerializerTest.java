package com.ratelimiter.core.serializer;

import com.ratelimiter.core.limit.RequestLimitRule;
import com.ratelimiter.core.serializer.LimitRuleJsonSerializer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LimitRuleJsonSerializerTest {

    @Test
    void testEncode() {
        List<RequestLimitRule> rules = Arrays.asList(
                new RequestLimitRule(1000, 10, 2),
                new RequestLimitRule(2000, 20, 3)
        );

        String expectedJson = "[[1000,10,2],[2000,20,3]]";
        assertEquals(expectedJson, LimitRuleJsonSerializer.encode(rules));
    }
}
