package com.weather.adapter.out.db;

import com.weather.domain.model.WeatherReading;
import com.weather.domain.port.out.WeatherRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class DatabaseAdapter implements WeatherRepository {

    @Override
    @Transactional   
    public WeatherReading save(WeatherReading reading) {
        WeatherEntity entity = WeatherEntity.fromDomain(reading);
        entity.persist();  
        return entity.toDomain();  
    }

    @Override
    public List<WeatherReading> findByCity(String city) {
        return WeatherEntity.findByCityOrdered(city)
               .stream()
               .map(WeatherEntity::toDomain)  
               .collect(Collectors.toList());
    }

    @Override
    public Optional<WeatherReading> findLatestByCity(String city) {
        return WeatherEntity.findLatestByCity(city)
               .map(WeatherEntity::toDomain);
    }
}