package com.weather.adapter.db;

import com.weather.adapter.out.db.DatabaseAdapter;
import com.weather.domain.model.WeatherReading;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DatabaseAdapterIT {

    @Inject
    DatabaseAdapter databaseAdapter;

    @Test
    void save_thenFindByCity_shouldReturnSavedReading() {
        WeatherReading reading = new WeatherReading(
            "TestCity", 25.0, 10.0, "Clear sky", LocalDateTime.now()
        );

        
        WeatherReading saved = databaseAdapter.save(reading);

        
        assertNotNull(saved.getId());

        
        List<WeatherReading> readings = databaseAdapter.findByCity("TestCity");
        assertFalse(readings.isEmpty());
        assertEquals("Clear sky", readings.get(0).getWeatherDescription());
    }

    @Test
    void findLatestByCity_whenMultipleReadings_shouldReturnMostRecent() {
        LocalDateTime earlier = LocalDateTime.now().minusHours(2);
        LocalDateTime later   = LocalDateTime.now();

        databaseAdapter.save(new WeatherReading("SortCity", 20.0, 5.0, "Fog",     earlier));
        databaseAdapter.save(new WeatherReading("SortCity", 25.0, 8.0, "Overcast", later));

        Optional<WeatherReading> latest = databaseAdapter.findLatestByCity("SortCity");

        assertTrue(latest.isPresent());
        assertEquals("Overcast", latest.get().getWeatherDescription());
    }
}