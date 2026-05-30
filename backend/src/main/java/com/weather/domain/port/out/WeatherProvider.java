package com.weather.domain.port.out;

import com.weather.domain.model.WeatherReading;

public interface WeatherProvider {
    /**
     * @throws CityNotFoundException 
     * @throws WeatherFetchException
     */
    WeatherReading fetchCurrentWeather(String city);
}