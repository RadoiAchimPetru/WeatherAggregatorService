package com.weather.adapter.in.rest;

import com.weather.domain.exception.CityNotFoundException;
import com.weather.domain.exception.WeatherFetchException;
import com.weather.domain.model.WeatherReading;
import com.weather.domain.port.in.WeatherUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/weather")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WeatherResource {

    @Inject
    WeatherUseCase weatherUseCase;

    @POST
    @Path("/fetch")
    public Response fetchWeather(@QueryParam("city") String city) {
        if (city == null || city.isBlank()) {
            return Response.status(400)
                           .entity("{\"error\": \"City parameter is mandatory\"}")
                           .build();
        }
        try {
            WeatherReading reading = weatherUseCase.fetchAndStore(city);
            return Response.status(201).entity(toDto(reading)).build();
        } catch (CityNotFoundException e) {
            return Response.status(404)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } catch (WeatherFetchException e) {
            e.printStackTrace();
            return Response.status(502)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

  
    @GET
    @Path("/{city}")
    public Response getReadings(@PathParam("city") String city) {
        List<WeatherReadingDto> readings = weatherUseCase.getReadingsForCity(city)
            .stream()
            .map(this::toDto)
            .toList();
        return Response.ok(readings).build();
    }

    
    @GET
    @Path("/{city}/latest")
    public Response getLatest(@PathParam("city") String city) {
        return weatherUseCase.getLatestReading(city)
            .map(r -> Response.ok(toDto(r)).build())
            .orElse(Response.status(404)
                            .entity("{\"error\": \"Nothing found\"}")
                            .build());
    }

    
    private WeatherReadingDto toDto(WeatherReading r) {
        return new WeatherReadingDto(
            r.getId(), r.getCity(), r.getTemperature(),
            r.getWindSpeed(), r.getWeatherDescription(),
            r.getFetchedAt().toString()
        );
    }

   
    public record WeatherReadingDto(
        Long id, String city, double temperature,
        double windSpeed, String weatherDescription, String fetchedAt
    ) {}
}