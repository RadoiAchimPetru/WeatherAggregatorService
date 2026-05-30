package com.weather.domain;

import com.weather.domain.exception.CityNotFoundException;
import com.weather.domain.exception.WeatherFetchException;
import com.weather.domain.model.WeatherReading;
import com.weather.domain.port.out.WeatherProvider;
import com.weather.domain.port.out.WeatherRepository;
import com.weather.domain.service.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    
    private WeatherProvider mockProvider;
    private WeatherRepository mockRepository;
    private WeatherServiceImpl service;

    @BeforeEach
    void setUp() {
        mockProvider   = mock(WeatherProvider.class);
        mockRepository = mock(WeatherRepository.class);
        
        service = new WeatherServiceImpl(mockProvider, mockRepository);
    }

    @Test
    void fetchAndStore_shouldCallProviderThenRepository() {
        
        WeatherReading fakeReading = new WeatherReading(
            "Timisoara", 22.4, 14.2, "Overcast", LocalDateTime.now()
        );
        WeatherReading savedReading = new WeatherReading(
            1L, "Timisoara", 22.4, 14.2, "Overcast", LocalDateTime.now()
        );

        
        when(mockProvider.fetchCurrentWeather("Timisoara")).thenReturn(fakeReading);
        when(mockRepository.save(fakeReading)).thenReturn(savedReading);

       
        WeatherReading result = service.fetchAndStore("Timisoara");

        
        assertEquals("Timisoara", result.getCity());
        assertEquals(22.4, result.getTemperature());
        assertEquals(1L, result.getId());

      
        verify(mockProvider, times(1)).fetchCurrentWeather("Timisoara");
        verify(mockRepository, times(1)).save(fakeReading);
    }

    @Test
    void fetchAndStore_whenCityNotFound_shouldThrowCityNotFoundException() {
       
        when(mockProvider.fetchCurrentWeather("CiudadInventata"))
            .thenThrow(new CityNotFoundException("CiudadInventata"));

       
        assertThrows(
            CityNotFoundException.class,
            () -> service.fetchAndStore("CiudadInventata")
        );

       
        verifyNoInteractions(mockRepository);
    }

    @Test
    void fetchAndStore_whenApiFails_shouldThrowWeatherFetchException() {
        when(mockProvider.fetchCurrentWeather("Timisoara"))
            .thenThrow(new WeatherFetchException("Timeout", new RuntimeException()));

        assertThrows(
            WeatherFetchException.class,
            () -> service.fetchAndStore("Timisoara")
        );

        verifyNoInteractions(mockRepository);
    }

    @Test
    void getLatestReading_whenNoReadings_shouldReturnEmpty() {
        when(mockRepository.findLatestByCity("EmptyCity"))
            .thenReturn(Optional.empty());

        Optional<WeatherReading> result = service.getLatestReading("EmptyCity");

        assertTrue(result.isEmpty());
    }
}