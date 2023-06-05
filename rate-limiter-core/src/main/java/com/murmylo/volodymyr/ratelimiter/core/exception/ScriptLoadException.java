package com.murmylo.volodymyr.ratelimiter.core.exception;

public class ScriptLoadException extends RuntimeException {
    public ScriptLoadException(String message) {
        super(message);
    }

    public ScriptLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
