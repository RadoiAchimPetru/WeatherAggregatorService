package com.weather.domain.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super("City '" + city + "' was not found.");
    }
}