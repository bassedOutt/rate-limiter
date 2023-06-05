package com.murmylo.volodymyr.ratelimiter.core.serializer;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.murmylo.volodymyr.ratelimiter.core.limit.RequestLimitRule;

public class LimitRuleJsonSerializer {

    public static String encode(Iterable<RequestLimitRule> rules) {
        JsonArray jsonArray = Json.array().asArray();
        rules.forEach(rule -> jsonArray.add(toJsonArray(rule)));
        return jsonArray.toString();
    }

    private static JsonArray toJsonArray(RequestLimitRule rule) {
        return Json.array().asArray()
                .add(rule.getDuration())
                .add(rule.getLimit())
                .add(rule.getPrecision());
    }
}