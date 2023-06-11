package com.ratelimiter.core.mapper;

import com.ratelimiter.core.limit.RateLimitRule;
import com.ratelimiter.core.limit.RequestLimitRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestLimitRuleMapperTest {

    @Test
    void verifyMappingIsCorrect() {
        // Create a RateLimitRule annotation for testing
        RateLimitRule rule = mock(RateLimitRule.class);
        when(rule.limit()).thenReturn(10L);
        when(rule.duration()).thenReturn(1000);
        when(rule.precision()).thenReturn(2);
        when(rule.name()).thenReturn("myRule");

        // Expected RequestLimitRule object
        RequestLimitRule expectedRule = new RequestLimitRule(1000, 10, 2, "myRule");

        // Invoke the mapFromAnnotation method
        RequestLimitRule mappedRule = RequestLimitRuleMapper.mapFromAnnotation(rule);

        // Verify the expected RequestLimitRule is returned
        assertEquals(expectedRule, mappedRule);
    }
}

