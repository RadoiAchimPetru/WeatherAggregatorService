package com.weather.adapter.out.db;

import com.weather.domain.model.WeatherReading;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "weather_readings")
public class WeatherEntity extends PanacheEntity {

    @Column(nullable = false)
    public String city;

    @Column(nullable = false)
    public double temperature;

    @Column(nullable = false, name = "wind_speed")
    public double windSpeed;

    @Column(nullable = false, name = "weather_description")
    public String weatherDescription;

    @Column(nullable = false, name = "fetched_at")
    public LocalDateTime fetchedAt;

    
    public static List<WeatherEntity> findByCityOrdered(String city) {
        return find("city = ?1 ORDER BY fetchedAt DESC", city).list();
    }

    public static Optional<WeatherEntity> findLatestByCity(String city) {
        return find("city = ?1 ORDER BY fetchedAt DESC", city)
               .firstResultOptional();
    }

    
    public WeatherReading toDomain() {
        return new WeatherReading(id, city, temperature,
                                  windSpeed, weatherDescription, fetchedAt);
    }

    
    public static WeatherEntity fromDomain(WeatherReading reading) {
        WeatherEntity entity = new WeatherEntity();
        entity.city = reading.getCity();
        entity.temperature = reading.getTemperature();
        entity.windSpeed = reading.getWindSpeed();
        entity.weatherDescription = reading.getWeatherDescription();
        entity.fetchedAt = reading.getFetchedAt();
        return entity;
    }
}