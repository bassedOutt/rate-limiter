package com.example.ratelimiter.limit;

import java.time.Duration;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Defines a limit rule that can support regular and token bucket rate limits.
 */
public class RequestLimitRule {

    private final int duration;
    private final long limit;
    private final int precision;
    private final String name;

    private RequestLimitRule(int duration, long limit, int precision) {
        this(duration, limit, precision, null);
    }

    private RequestLimitRule(int duration, long limit, int precision, String name) {
        this.duration = duration;
        this.limit = limit;
        this.precision = precision;
        this.name = name;
    }

    private static void checkDuration(Duration duration) {
        requireNonNull(duration, "duration can not be null");
        if (Duration.ofSeconds(1).compareTo(duration) > 0) {
            throw new IllegalArgumentException("duration must be great than 1 second");
        }
    }

    /**
     * Initialise a request rate limit. Imagine the whole duration window as being one large bucket with a single count.
     *
     * @param duration The time the limit will be applied over. The duration must be greater than 1 second.
     * @param limit    A number representing the maximum operations that can be performed in the given duration.
     * @return A limit rule.
     */
    public static RequestLimitRule of(Duration duration, long limit) {
        checkDuration(duration);
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be greater than zero.");
        }
        int durationSeconds = (int) duration.getSeconds();
        return new RequestLimitRule(durationSeconds, limit, durationSeconds);
    }

    /**
     * Controls (approximate) sliding window precision. A lower duration increases precision and minimises the Thundering herd problem - https://en.wikipedia.org/wiki/Thundering_herd_problem
     *
     * @param precision Defines the time precision that will be used to approximate the sliding window. The precision must be greater than 1 second.
     * @return a limit rule
     */
    public RequestLimitRule withPrecision(Duration precision) {
        checkDuration(precision);
        return new RequestLimitRule(this.duration, this.limit, (int) precision.getSeconds(), this.name);
    }

    /**
     * Applies a name to the rate limit that is useful for metrics.
     *
     * @param name Defines a descriptive name for the rule limit.
     * @return a limit rule
     */
    public RequestLimitRule withName(String name) {
        return new RequestLimitRule(this.duration, this.limit, this.precision, name);
    }

    /**
     * Returns the limits duration in seconds.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns the limits precision in seconds.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the limit.
     */
    public long getLimit() {
        return limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestLimitRule that)) {
            return false;
        }
        return duration == that.duration
                && limit == that.limit
                && precision == that.precision
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, limit, precision, name);
    }
}
