package com.weather.domain.model;

import java.time.LocalDateTime;

public class WeatherReading {
    private Long id;
    private String city;
    private double temperature;      
    private double windSpeed;        
    private String weatherDescription; 
    private LocalDateTime fetchedAt;

    
    public WeatherReading(String city, double temperature,
                          double windSpeed, String weatherDescription,
                          LocalDateTime fetchedAt) {
        this.city = city;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.weatherDescription = weatherDescription;
        this.fetchedAt = fetchedAt;
    }

    
    public WeatherReading(Long id, String city, double temperature,
                          double windSpeed, String weatherDescription,
                          LocalDateTime fetchedAt) {
        this(city, temperature, windSpeed, weatherDescription, fetchedAt);
        this.id = id;
    }

    
    public Long getId() { return id; }
    public String getCity() { return city; }
    public double getTemperature() { return temperature; }
    public double getWindSpeed() { return windSpeed; }
    public String getWeatherDescription() { return weatherDescription; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
}