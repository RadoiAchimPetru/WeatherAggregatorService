package com.weather.domain.exception;

public class WeatherFetchException extends RuntimeException {
    public WeatherFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}