package com.ratelimiter.example.exception;

public class MovieInfoNotfoundException extends RuntimeException{
    public MovieInfoNotfoundException(String message) {
        super(message);
    }
}
