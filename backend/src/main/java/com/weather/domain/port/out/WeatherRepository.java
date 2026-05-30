package com.weather.domain.port.out;

import com.weather.domain.model.WeatherReading;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository {
    WeatherReading save(WeatherReading reading);
    List<WeatherReading> findByCity(String city);      
    Optional<WeatherReading> findLatestByCity(String city);
}