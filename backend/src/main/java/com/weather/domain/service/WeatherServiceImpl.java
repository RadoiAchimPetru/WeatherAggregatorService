package com.weather.domain.service;

import com.weather.domain.model.WeatherReading;
import com.weather.domain.port.in.WeatherUseCase;
import com.weather.domain.port.out.WeatherProvider;
import com.weather.domain.port.out.WeatherRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WeatherServiceImpl implements WeatherUseCase {

    
    private final WeatherProvider weatherProvider;
    
    private final WeatherRepository weatherRepository;

    
    public WeatherServiceImpl(WeatherProvider weatherProvider,
                               WeatherRepository weatherRepository) {
        this.weatherProvider = weatherProvider;
        this.weatherRepository = weatherRepository;
    }

    @Override
    public WeatherReading fetchAndStore(String city) {
        
        WeatherReading reading = weatherProvider.fetchCurrentWeather(city);
        
        return weatherRepository.save(reading);
    }

    @Override
    public List<WeatherReading> getReadingsForCity(String city) {
        return weatherRepository.findByCity(city);
    }

    @Override
    public Optional<WeatherReading> getLatestReading(String city) {
        return weatherRepository.findLatestByCity(city);
    }
}