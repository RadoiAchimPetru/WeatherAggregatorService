package com.weather.adapter.out.http;

import com.weather.domain.exception.CityNotFoundException;
import com.weather.domain.exception.WeatherFetchException;
import com.weather.domain.model.WeatherReading;
import com.weather.domain.port.out.WeatherProvider;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class OpenMeteoAdapter implements WeatherProvider {

    
    @ConfigProperty(name = "openmeteo.geocoding.url",
                    defaultValue = "https://geocoding-api.open-meteo.com/v1/search")
    String geocodingUrl;

    @ConfigProperty(name = "openmeteo.weather.url",
                    defaultValue = "https://api.open-meteo.com/v1/forecast")
    String weatherUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    
    private static final Map<Integer, String> WMO_CODES = Map.ofEntries(
        Map.entry(0,  "Clear sky"),
        Map.entry(1,  "Mainly clear"),
        Map.entry(2,  "Partly cloudy"),
        Map.entry(3,  "Overcast"),
        Map.entry(45, "Fog"),
        Map.entry(48, "Depositing rime fog"),
        Map.entry(51, "Drizzle: light"),
        Map.entry(53, "Drizzle: moderate"),
        Map.entry(55, "Drizzle: dense"),
        Map.entry(61, "Rain: slight"),
        Map.entry(63, "Rain: moderate"),
        Map.entry(65, "Rain: heavy"),
        Map.entry(71, "Snow: slight"),
        Map.entry(73, "Snow: moderate"),
        Map.entry(75, "Snow: heavy"),
        Map.entry(80, "Rain showers: slight"),
        Map.entry(81, "Rain showers: moderate"),
        Map.entry(82, "Rain showers: violent"),
        Map.entry(95, "Thunderstorm"),
        Map.entry(99, "Thunderstorm with hail")
    );

    @Override
    public WeatherReading fetchCurrentWeather(String city) {
        try {
            
            double[] coords = resolveCoordinates(city);

            
            return fetchWeather(city, coords[0], coords[1]);

        } catch (CityNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new WeatherFetchException(
                "Eroare la apelul Open-Meteo pentru: " + city, e);
        }
    }

    private double[] resolveCoordinates(String city) throws Exception {
        String url = geocodingUrl + "?name=" + city + "&count=1&language=en&format=json";

        HttpResponse<String> response = httpClient.send(
            HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        );

      
        if (!response.body().contains("\"results\"") ||
             response.body().contains("\"results\":[]") ||
             response.body().contains("\"results\":{}")) {
            throw new CityNotFoundException(city);
        }

        
        double lat = extractDouble(response.body(), "latitude");
        double lon = extractDouble(response.body(), "longitude");
        return new double[]{lat, lon};
    }

    private WeatherReading fetchWeather(String city, double lat, double lon)
            throws Exception {
        String url = weatherUrl + "?latitude=" + lat + "&longitude=" + lon
                   + "&current_weather=true&wind_speed_unit=kmh";

        HttpResponse<String> response = httpClient.send(
            HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        );

        double temperature = extractDouble(response.body(), "temperature");
        double windspeed   = extractDouble(response.body(), "windspeed");
        int    weathercode = (int) extractDouble(response.body(), "weathercode");

        String description = WMO_CODES.getOrDefault(weathercode, "Unknown");

        return new WeatherReading(city, temperature, windspeed,
                                  description, LocalDateTime.now());
    }

    
    private double extractDouble(String json, String key) {
        int idx = json.indexOf("\"" + key + "\":");
        if (idx == -1) return 0;
        int start = idx + key.length() + 3;
        int end = start;
        while (end < json.length() &&
               (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) {
            end++;
        }
        return Double.parseDouble(json.substring(start, end));
    }
}