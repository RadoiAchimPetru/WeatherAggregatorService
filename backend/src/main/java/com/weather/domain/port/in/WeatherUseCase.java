package com.weather.domain.port.in;

import com.weather.domain.model.WeatherReading;
import java.util.List;
import java.util.Optional;

public interface WeatherUseCase {

    WeatherReading fetchAndStore(String city);
    List<WeatherReading> getReadingsForCity(String city);
    Optional<WeatherReading> getLatestReading(String city);

}